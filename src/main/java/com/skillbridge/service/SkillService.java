package com.skillbridge.service;

import com.skillbridge.entity.Skill;

import java.util.List;

public interface SkillService {
    List<Skill> getAllSkills();

    List<Skill> getSkillByName(String name);

    List<Skill> getAllSkillsByCategory();

}
