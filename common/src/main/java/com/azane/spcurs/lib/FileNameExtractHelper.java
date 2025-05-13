package com.azane.spcurs.lib;

public final class FileNameExtractHelper
{
    private FileNameExtractHelper(){}
    public static String getPureFileName(String path)
    {
        path = path.substring(path.lastIndexOf('/') + 1);
        return path.replaceFirst("[.][^.]+$", "");
    }
    public static String getHeadFolderFiltered(String path)
    {
        path = !path.contains("/") ? path : path.substring(path.indexOf("/")+1);
        return path.replaceFirst("[.][^.]+$", "");
    }
}
