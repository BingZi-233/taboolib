package taboolib.common5;

import taboolib.common.Isolated;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 只读Lore对象存储Map
 * 用于物品Lore前缀识别, 在Map中储存lore关键词和对象, 然后丢入装备上的lore即可高效返回对象
 * 用法和 HashMap类似, 适合上百条乃至上千条lore的快速前缀判断.
 * 比如, 你有"物理伤害", "法术伤害", "心理伤害" 等上百种属性,
 * "&b&l[&e&l属性&b&l]&c&l物理伤害: &a20"
 * "草泥马透透透物理伤害: &b20"
 * 现在要从中准确提取出 "物理伤害"和":20" (注："物理伤害"这四个字中间, 只允许有空格或颜色代码)
 * 那么用这个LoreMap, 只需调用 getMatchResult(lore),
 * 你将会在几个毫秒内得到匹配结果, 远远快于循环contains和正则表达式
 * 注意, 要完全匹配, 推荐直接用HashMap的containsKey(lore)
 * ============================================
 * 用法示例: 属性插件
 * 假定你通过抽象类定义了属性
 * public abstract class MyAttribute{...}
 * 并且定义了子类
 * public class Damage extend MyAttribute{...}
 * public class Speed extend MyAttribute{...}
 * public class Health extend MyAttribute{...}
 * 你就可以初始化一个属性Map:
 * public static LoreMap<MyAttribute> attrMap = new LoreMap(true, true, true, false);
 * 这个属性Map无视颜色代码, 无视前缀, 无视空格.
 * 然后向Map中添加lore和属性
 * attrMap.put("物理伤害", new Damage())
 * attrMap.put("速度", new Health())
 * attrMap.put("生命值", new Speed())
 * 之后在用户用装备的时候,
 * for (String lore: meta.getLore()) {
 *     // 在Map中匹配属性
 *     LoreMap.Result<MyAttribute> matchResult = attrMap.getMatchResult(lore)
 *     // 如果没匹配到 处理下一条
 *     if (matchResult.obj == null) {
 *         continue;
 *     }
 *     // 匹配到了的话, 取属性
 *     MyAttribute attr = matchResult.obj;
 *     // 取属性lore右边剩下没匹配完的
 *     String remain = matchResult.remain;
 *     // 没匹配完的啥也没有, 说明属性右边没数字, 跳过
 *     if (remain==null) continue;
 *     // 处理属性
 *     // ....
 * \}
 * 整个过程是线程安全的. 你可以在异步线程中使用它
 *
 * @author YiMiner
 **/
@Isolated
public class LoreMap<T> {

    private final boolean ignorePrefix;
    private final boolean ignoreSpace;
    private final boolean ignoreColor;
    private final boolean suffix;
    private final TrieNode root = new TrieNode();

    /**
     * 初始化一个LoreMap
     *
     * @param ignoreSpace  是否无视空格
     * @param ignoreColor  是否无视颜色
     * @param ignorePrefix 是否无视前缀
     *
     **/
    public LoreMap(boolean ignoreSpace, boolean ignoreColor, boolean ignorePrefix, boolean suffix) {
        this.ignoreSpace = ignoreSpace;
        this.ignoreColor = ignoreColor;
        this.ignorePrefix = ignorePrefix;
        this.suffix = !ignorePrefix && suffix;
    }

    /**
     * 初始化一个LoreMap, 匹配lore前缀, 无视颜色和空格
     **/
    public LoreMap() {
        this(true, true, false, false);
    }

    /**
     * 向LoreMap中放入lore和对应的对象
     *
     * @param lore  物品lore
     * @param value 要放入的对象
     **/
    public void put(String lore, T value) {
        if (suffix) {
            lore = new StringBuilder(lore).reverse().toString();
        }
        TrieNode current = root;
        for (int i = 0; i < lore.length() - 1; ++i) {
            char currentChar = lore.charAt(i);
            if (currentChar == ' ') {
                if(ignoreSpace) {
                    continue;
                }
            }else if ((currentChar == '&' || currentChar == '§') && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(lore.charAt(i + 1)) > -1) {
                if (!ignoreColor) {
                    current = putLoreChar(current, new LoreChar('§'));
                    current = putLoreChar(current, new LoreChar(Character.toLowerCase(lore.charAt(i + 1))));
                }
                i++;
                continue;
            }
            current = putLoreChar(current, new LoreChar(currentChar));
        }
        char finalChar = lore.charAt(lore.length() - 1);
        if (!ignoreSpace || finalChar != ' ') {
            current = putLoreChar(current, new LoreChar(finalChar));
        }
        current.obj = value;
    }

    /**
     * 查询lore对应的对象
     * 贪婪匹配（默认匹配最长的前缀）
     *
     * @return 查到的对象. 无则返回null
     **/

    public T get(String lore) {
        MatchResult<T> result = getMatchResult(lore);
        return result != null ? result.obj : null;
    }

    /**
     * 查询lore对应的匹配结果
     * 贪婪匹配（默认匹配最长的前缀）
     *
     * @return 查到的匹配结果. obj为对象，remain为匹配剩下的string. 无则返回null
     **/

    public MatchResult<T> getMatchResult(String lore) {
        int depth;
        TrieNode current = root;
        MatchResult<T> result = null;
        if (suffix) {
            depth = lore.length() - 1;
            while (depth >= 0) {
                char currentChar = lore.charAt(depth);
                if (ignoreSpace && currentChar == ' ') {
                    depth--;
                    continue;
                }
                if (ignoreColor && depth >= 1 && lore.charAt(depth - 1) == '§' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(currentChar) > -1) {
                    depth -= 2;
                    continue;
                }
                LoreChar c = new LoreChar(currentChar);
                TrieNode node = current.child.get(c);
                if (node == null) {
                    return result;
                }
                if (node.obj != null) {
                    result = new MatchResult<>(depth > 0 ? lore.substring(0, depth) : null, node.obj);
                }
                current = node;
                depth--;
            }
        } else {
            depth = 0;
            if (ignorePrefix) {
                while (depth < lore.length()) {
                    if (root.child.containsKey(new LoreChar(lore.charAt(depth)))) {
                        break;
                    }
                    depth++;
                }
            }
            while (depth < lore.length()) {
                char currentChar = lore.charAt(depth);
                if (currentChar == ' ') {
                    if (ignoreSpace) {
                        depth++;
                        continue;
                    }
                } else if (currentChar == '§' && ignoreColor && depth < lore.length() - 1
                        && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(lore.charAt(depth + 1)) > -1) {
                    depth += 2;
                    continue;
                }
                LoreChar c = new LoreChar(currentChar);
                TrieNode node = current.child.get(c);
                if (node == null) {
                    return result;
                }
                if (node.obj != null) {
                    result = new MatchResult<>(depth < lore.length() - 1 ? lore.substring(depth + 1) : null, node.obj);
                }
                current = node;
                depth++;
            }
        }
        return result;
    }

    public boolean isEmpty() {
        return root.child.isEmpty();
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    public void clear() {
        root.child.clear();
    }

    private TrieNode putLoreChar(TrieNode current, LoreChar c) {
        if (current.child.containsKey(c)) {
            return current.child.get(c);
        } else {
            TrieNode node = new TrieNode();
            node.depth++;
            node.pre = current;
            current.child.put(c, node);
            return node;
        }
    }

    private static class LoreChar {
        private final char c;

        public LoreChar(char c) {
            this.c = c;
        }

        public char get() {
            return c;
        }

        @Override
        public int hashCode() {
            return c;
        }

        @Override
        public boolean equals(Object o) {
            return (o instanceof LoreChar) && ((LoreChar) o).c == c;
        }
    }

    public static class MatchResult<T> {
        public final String remain;
        public final T obj;

        public MatchResult(String remain, T obj) {
            this.remain = remain;
            this.obj = obj;
        }
    }

    public class TrieNode {
        final ConcurrentHashMap<LoreChar, TrieNode> child = new ConcurrentHashMap<>();
        TrieNode pre = null;
        T obj = null;
        int depth = 0;
    }

}
