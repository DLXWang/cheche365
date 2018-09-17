package com.cheche365.cheche.core.model.agent

import com.cheche365.cheche.core.context.ApplicationContextHolder
import com.cheche365.cheche.core.repository.AgentLevelRepository
import com.cheche365.cheche.core.util.RuntimeUtil
import groovy.transform.Canonical

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
@Canonical(includes = ['id'])
class AgentLevel implements Serializable {
    private static final long serialVersionUID = 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id

    @Column(columnDefinition = "VARCHAR(100)")
    String description

    /**
     * param isLeaf 判断是否也叶子节点，即：是不是代理人关系中的 业务员
     * isLeaf=1  表示是叶子节点
     */
    @Column(columnDefinition = "tinyint(1)")
    Boolean isLeaf


    static class Enum {

        public static AgentLevel SALE_DIRECTOR_1
        public static AgentLevel SALE_MANAGER_2

        static {
            RuntimeUtil.loadEnum(AgentLevelRepository, AgentLevel, Enum)
        }

        static AgentLevel byIdFindAgentLevel(Long id) {
            allList().find { it -> it.id == id }
        }

        static AgentLevel nextLevel(AgentLevel currentLevel) {
            currentLevel ? allList().find { it -> it.id == currentLevel.id + 1l } : null
        }

        static List<AgentLevel> allList() {
            ApplicationContextHolder.getApplicationContext().getBean('agentLevelRepository').findAll()
        }

    }

    static String agentCodeLike(id) {
        return '.' + String.valueOf(id) + '.'
    }
}
