package com.wtu.service.impl;

import com.wtu.vo.EnhancedPromptsVO;
import com.wtu.service.Neo4jPromptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Neo4j提示词生成服务实现类
 * 基于Neo4j知识图谱的服装提示词生成服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class Neo4jPromptServiceImpl implements Neo4jPromptService {

    private final Driver neo4jDriver;

    private static final String TEMPLATE = "请为我生成一件的%s风格%s，图案为%s，并搭配%s元素，适合%s场合。";

    @Override
    public EnhancedPromptsVO getEnhancedPrompts(String garmentName, String occasion, int numPrompts) {
        try (Session session = neo4jDriver.session()) {
            log.info("开始生成提示词 - 服装: {}, 场合: {}, 数量: {}", garmentName, occasion, numPrompts);

            Features features = queryGarmentFeatures(session, garmentName);

            String formatted = buildFormattedPrompt(garmentName, features);

            List<String> randomPrompts = new ArrayList<>();
            for (int i = 0; i < numPrompts; i++) {
                String randomPrompt = generateRandomPrompt(garmentName, features, occasion);
                randomPrompts.add(randomPrompt);
            }

            Map<String, List<String>> featuresMap = new HashMap<>();
            featuresMap.put("colors", features.colors);
            featuresMap.put("styles", features.styles);
            featuresMap.put("patterns", features.patterns);
            featuresMap.put("elements", features.elements);

            log.info("提示词生成成功 - 服装: {}", garmentName);
            return new EnhancedPromptsVO(
                    garmentName,
                    formatted,
                    randomPrompts,
                    featuresMap
            );
        } catch (Exception e) {
            log.error("获取增强提示词失败 - 服装: {}, 错误: {}", garmentName, e.getMessage(), e);
            throw new RuntimeException("获取增强提示词失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean testConnection() {
        try (Session session = neo4jDriver.session()) {
            session.run("RETURN 1");
            log.info("Neo4j连接测试成功");
            return true;
        } catch (Exception e) {
            log.error("Neo4j连接测试失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 查询服装特征
     */
    private Features queryGarmentFeatures(Session session, String garmentName) {
        List<String> colors = queryList(session,
                "MATCH (g:Garment {name: $name})-[:HAS_COLOR]->(c:Color) RETURN c.name AS name",
                Map.of("name", garmentName), "name");

        List<String> styles = queryList(session,
                "MATCH (g:Garment {name: $name})-[:HAS_STYLE]->(s:Style) RETURN s.name AS name",
                Map.of("name", garmentName), "name");

        List<String> patterns = queryList(session,
                "MATCH (g:Garment {name: $name})-[:HAS_PATTERN]->(p:Pattern) RETURN p.name AS name",
                Map.of("name", garmentName), "name");

        List<String> elements = queryList(session,
                "MATCH (g:Garment {name: $name})-[:HAS_ELEMENT]->(e:Element) RETURN e.name AS name",
                Map.of("name", garmentName), "name");

        return new Features(colors, styles, patterns, elements);
    }

    /**
     * 查询列表数据
     */
    private List<String> queryList(Session session, String cypher, Map<String, Object> params, String column) {
        return session.run(cypher, params)
                .list((Record r) -> r.get(column).asString())
                .stream()
                .distinct()
                .toList();
    }

    /**
     * 构建格式化提示词
     */
    private String buildFormattedPrompt(String originalPrompt, Features features) {
        String colors = String.join(",", features.colors);
        String styles = String.join(",", features.styles);
        String patterns = String.join(",", features.patterns);
        String elements = String.join(",", features.elements);

        return "【原prompt：（" + originalPrompt + "） 推荐prompt："
                + "颜色{" + colors + "}，"
                + "风格{" + styles + "}，"
                + "图案{" + patterns + "}，"
                + "元素{" + elements + "}，】";
    }

    /**
     * 生成随机提示词
     */
    private String generateRandomPrompt(String garmentName, Features features, String occasion) {
        String style = randomOrDefault(features.styles);
        String pattern = randomOrDefault(features.patterns);
        String element = randomOrDefault(features.elements);
        return String.format(Locale.ROOT, TEMPLATE, style, garmentName, pattern, element, occasion);
    }

    /**
     * 随机选择或返回默认值
     */
    private String randomOrDefault(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "默认";
        }
        int idx = new Random().nextInt(list.size());
        return list.get(idx);
    }

    /**
     * 特征数据类
     */
    private static class Features {
        public final List<String> colors;
        public final List<String> styles;
        public final List<String> patterns;
        public final List<String> elements;

        public Features(List<String> colors, List<String> styles, List<String> patterns, List<String> elements) {
            this.colors = colors != null ? colors : List.of();
            this.styles = styles != null ? styles : List.of();
            this.patterns = patterns != null ? patterns : List.of();
            this.elements = elements != null ? elements : List.of();
        }
    }
}
