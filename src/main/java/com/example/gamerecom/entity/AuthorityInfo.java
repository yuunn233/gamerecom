package com.example.gamerecom.entity;


import org.springframework.ui.Model;

import java.util.List;

//权限列表类
public class AuthorityInfo {
    private Integer level;
    private String name;
    private List<Model> models;
    public static class Model{
        private  Integer modelId;
        //列表权限
        private List<Integer> operation;

        public List<Integer> getOperation() {
            return operation;
        }

        public void setOperation(List<Integer> operation) {
            this.operation = operation;
        }

        public Integer getModelId() {
            return modelId;
        }

        public void setModelId(Integer modelId) {
            this.modelId = modelId;
        }

    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Model> getModels() {
        return models;
    }

    public void setModels(List<Model> models) {
        this.models = models;
    }
}
