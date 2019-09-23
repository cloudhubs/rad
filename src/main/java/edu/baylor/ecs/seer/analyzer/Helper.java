package edu.baylor.ecs.seer.analyzer;

import javassist.bytecode.annotation.Annotation;
import org.apache.commons.io.FilenameUtils;

public class Helper {
    public static String mergePaths(String classPath, String methodPath) {
        String path = FilenameUtils.normalizeNoEndSeparator(FilenameUtils.concat(classPath, methodPath), true);
        if (!path.startsWith("/")) path = "/" + path;
        return path;
    }

    public static String getAnnotationValue(Annotation annotation, String member) {
        if (annotation.getMemberValue(member) == null) return null;
        String value = annotation.getMemberValue(member).toString();
        // System.out.println("###" + annotation.getTypeName() + " " + member + " " + value);
        return removeEnclosedQuotations(removeEnclosedBraces(value));
    }

    public static String removeEnclosedQuotations(String s) {
        if (s != null && s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static String removeEnclosedBraces(String s) {
        if (s != null && s.length() >= 2 && s.startsWith("{") && s.endsWith("}")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static String removeEnclosedSingleQuotations(String s) {
        if (s != null && s.length() >= 2 && s.startsWith("'") && s.endsWith("'")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static boolean matchUrl(String clientUrl, String serverUrl) {
        if (clientUrl == null || serverUrl == null) return false;
        return removeAmbiguity(clientUrl).equals(removeAmbiguity(serverUrl));
    }

    public static String removeAmbiguity(String url) {
        return url.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static String mergeUrlPath(String url, String path) {
        url = Helper.removeEnclosedSingleQuotations(url);
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
        if (path != null && path.length() > 1) url = url + path; // merge if path not empty
        return url;
    }
}
