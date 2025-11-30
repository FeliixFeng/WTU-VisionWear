package com.wtu.service;

import com.wtu.vo.EnhancedPromptsVO;

/**
 * Neo4j提示词生成服务接口
 * 基于Neo4j知识图谱的服装提示词生成服务
 */
public interface Neo4jPromptService {

    /**
     * 获取增强的提示词
     * 根据服装名称和场合，从Neo4j知识图谱中查询相关特征（颜色、风格、图案、元素），
     * 生成格式化提示词和多个随机提示词
     *
     * @param garmentName 服装名称（如：T恤、连衣裙）
     * @param occasion    适用场合（如：休闲、正式、运动）
     * @param numPrompts  生成随机提示词的数量
     * @return 增强提示词结果对象，包含格式化提示词、随机提示词列表和特征映射
     * @throws RuntimeException 当查询失败或数据库连接异常时抛出
     */
    EnhancedPromptsVO getEnhancedPrompts(String garmentName, String occasion, int numPrompts);

    /**
     * 测试Neo4j数据库连接
     * 执行简单的数据库查询以验证连接是否正常
     *
     * @return true表示连接成功，false表示连接失败
     */
    boolean testConnection();
}
