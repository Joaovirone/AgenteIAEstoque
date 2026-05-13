package com.AgentAIEstoque.application.service;

import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class DatabaseExecutionService {
    
    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> executarSqlDinamico(String sql){
        
        String upperSql = sql.toUpperCase();

        if (upperSql.contains("INSERT ") || upperSql.contains("UPDATE ") || 
            upperSql.contains("DELETE ") || upperSql.contains("DROP ") || 
            upperSql.contains("ALTER ") || upperSql.contains("TRUNCATE ")) {
            throw new SecurityException("Acesso negado: O agente de I.A. possui apenas permissão de leitura (SELECT).");
        }

        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar a consulta dinâmica no banco: " + e.getMessage());
        }
    }

}
