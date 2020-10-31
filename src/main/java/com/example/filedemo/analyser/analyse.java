package com.example.filedemo.analyser;
import java.io.File;
import org.apache.tika.Tika;

class algos extends Thread {

    String str;
    String pattern;
    int found;

    algos(String text, String pattern) {
        this.str = text;
        this.pattern = pattern;
    }

    public void run() {
        System.out.println("Checking for type : " + pattern);
        found = search(str, pattern);
        if (found != -1) {
            System.out.println("The file is of type : " + pattern);
        } else {
            System.out.println("The file is NOT : " + pattern);
        }
    }

    public static int search(String str, String pattern) {
        char[] strs = str.toCharArray();
        char[] patterns = pattern.toCharArray();

        int L = strs.length, N = patterns.length, i = 0, j = 0;
        if (N < 1)
            return 0;
        if (L < N)
            return -1;

        int[] lps = lps(pattern);

        while (i < L) {
            if (strs[i] == patterns[j]) {
                ++i;
                ++j;
                if (j == N)
                    return i - N;
            } else if (j > 0)
                j = lps[j - 1];
            else
                ++i;
        }
        return -1;
    }

    private static int[] lps(String pattern) {
        int j = 0, i = 1, L = pattern.length();
        int[] res = new int[L];
        char[] chars = pattern.toCharArray();
        while (i < L) {
            if (chars[i] == chars[j])
                res[i++] = ++j;
            else {
                int temp = i - 1;
                while (temp > 0) {
                    int prevLPS = res[temp];
                    if (chars[i] == chars[prevLPS]) {
                        res[i++] = prevLPS + 1;
                        j = prevLPS;
                        break;
                    } else
                        temp = prevLPS - 1;
                }
                if (temp <= 0) {
                    res[i++] = 0;
                    j = 0;
                }
            }
        }
        return res;
    }

}

public class analyse {
    public static String analyses(String fileDestination) throws Exception {
        File file = new File(fileDestination);//
        Tika tika = new Tika();
        String filetype = tika.detect(file);
        return filetype;
    }
}

