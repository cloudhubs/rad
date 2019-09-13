package edu.baylor.ecs.seer.analyzer;

public class Helper {
    static String mergePaths(String classPath, String methodPath) {
        // remove quotations and add slash
        classPath = addSlash(removeQuotations(classPath));
        methodPath = addSlash(removeQuotations(methodPath));

        // merge, remove double slash and add quotations
        return addQuotations(removeMultipleSlashes(classPath + methodPath));
    }

    static String removeQuotations(String str) {
        return str.replaceAll("\"", "");
    }

    static String addQuotations(String str) {
        return "\"" + str + "\"";
    }

    static String addSlash(String str) {
        return "/" + str;
    }

    static String removeMultipleSlashes(String str) {
        return str.replaceAll("[/]+", "/");
    }
}
