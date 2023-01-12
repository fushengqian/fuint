package com.fuint.repository.model.base;

/**
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
public class OpsExercise {

    private String exerId;

    private int courseId;

    private int difficultyLv;

    public String getExerId() {
        return exerId;
    }

    public void setExerId(String exerId) {
        this.exerId = exerId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getDifficultyLv() {
        return difficultyLv;
    }

    public void setDifficultyLv(int difficultyLv) {
        this.difficultyLv = difficultyLv;
    }
}
