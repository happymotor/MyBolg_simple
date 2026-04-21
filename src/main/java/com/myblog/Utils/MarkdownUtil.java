package com.myblog.Utils;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownUtil {
    // 1. 全局唯一的Markdown解析器
    private static final Parser PARSER = Parser.builder().build();
    // 2. 全局唯一的HTML渲染器
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

    // 3. 校验Markdown格式是否合法
    public static boolean isValid(String markdown) {
        try {
            // 解析Markdown文本
            Node document = PARSER.parse(markdown);
            // 渲染成HTML（测试是否能正常渲染）
            RENDERER.render(document);
            // 无异常 → 格式合法
            return true;
        } catch (Exception e) {
            // 有异常 → 格式错误
            return false;
        }
    }

    // 4. Markdown 转换为 HTML
    public static String toHtml(String markdown) {
        // 解析Markdown
        Node document = PARSER.parse(markdown);
        // 渲染并返回HTML字符串
        return RENDERER.render(document);
    }
}