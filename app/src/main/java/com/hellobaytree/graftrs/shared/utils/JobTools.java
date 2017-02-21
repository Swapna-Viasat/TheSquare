package com.hellobaytree.graftrs.shared.utils;

import com.hellobaytree.graftrs.shared.models.ExperienceType;
import com.hellobaytree.graftrs.shared.models.Job;
import com.hellobaytree.graftrs.shared.models.Qualification;
import com.hellobaytree.graftrs.shared.models.Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gherg on 2/19/17.
 */

public class JobTools {

    public static List<String> extractQualifications(Job job) {
        List<String> result = new ArrayList<>();
        if (null != job.qualifications) {
            if (!job.qualifications.isEmpty()) {
                for (Qualification q : job.qualifications) {
                    result.add(q.name);
                }
            }
        }
        return result;
    }

//    public static List<String> extractExpQualifications(Job job) {
//        List<String> result = new ArrayList<>();
//        if (null != job.) {
//            if (!job.qualifications.isEmpty()) {
//                for (Qualification q : job.qualifications) {
//                    result.add(q.name);
//                }
//            }
//        }
//        return result;
//    }

    public static List<String> extractSkills(Job job) {
        List<String> result = new ArrayList<>();
        if (null != job.skills) {
            if (!job.skills.isEmpty()) {
                for (Skill skill : job.skills) {
                    result.add(skill.name);
                }
            }
        }
        return result;
    }

    public static List<String> extractExperienceTypes(Job job) {
        List<String> result = new ArrayList<>();
        if (null != job.experienceTypes) {
            if (!job.experienceTypes.isEmpty()) {
                for (ExperienceType e : job.experienceTypes) {
                    result.add(e.name);
                }
            }
        }
        return result;
    }
}
