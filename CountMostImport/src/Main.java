import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;
/*
三、根据指定项目目录下（可以认为是java源文件目录）中，统计被import最多的类，前十个是什么。
 */

public class Main {

    private Map<String,Integer> importMap =new HashMap<String,Integer>();

    private class Pair implements Comparable<Pair>
    {
        String importName;
        int count;
        Pair(String name, int count)
        {
            importName = name;
            this.count = count;
        }

        public int compareTo(Pair o)
        {
            return o.count - count;
        }

        @Override
        public String toString()
        {
            return '"' + importName + '"' + ": " + count;
        }
    }

    /*
     *
     * @param dir
     */
    public void getFile(String dir)
    {
        if (dir == null)
        {
            return;
        }

        File file =new File(dir);
        if (!file.exists())
        {
            System.out.println("File Not Exists");
            return;
        }

        Stack<File> stack = new Stack<File>();
        stack.push(file);

        while (!stack.isEmpty())
        {
            file = stack.peek();
            stack.pop();

            for(File f :file.listFiles())
            {
                if (f.isFile())
                {
                    countImport(f);
                } else if (f.isDirectory()){
                    stack.push(f);
                }

            }
        }

    }

    /*
     *
     * @param file
     */
    public void countImport(File file)
    {
        if (!file.getName().contains(".java"))
        {
            return;
        }
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            boolean isImport = false;

            while ((line=br.readLine())!=null)
            {
                // 忽略 import static 行
                Pattern p = Pattern.compile("^import\\s+static\\s+");
                Matcher m = p.matcher(line.trim());
                if (m.find()) {
                    continue;
                }

                // 匹配 import 开始行
                p = Pattern.compile("^import\\s+");
                m = p.matcher(line.trim());
                if (m.find()) {
                    isImport = true;
                    String temp = line.trim().substring(6).replace(";", "");
                    if (importMap.containsKey(temp))
                    {
                        int count = importMap.get(temp);
                        count++;
                        importMap.put(temp,count);
                    } else {
                        importMap.put(temp,1);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null)
            {
                try {
                    br.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     */
    public void countTop10Import()
    {
        List<Pair> list = new ArrayList<Pair>();
        Set<String> importNames = importMap.keySet();
        for(String importName :importNames)
        {
            Pair pair = new Pair(importName,importMap.get(importName));
            list.add(pair);
        }

        Pair[] pairArray = new Pair[0];
        pairArray = list.toArray(pairArray);
        Arrays.sort(pairArray);
        int N=0;
        for (Pair pair : pairArray)
        {
            if (++N >10)
            {
                break;
            }
            System.out.println(pair);
        }
    }

    public static void main(String[] args) {
        String dirPath = "\\Users\\Administrator\\IdeaProjects\\CountMostImport\\src";
        Main c = new Main();
        c.getFile(dirPath);
        c.countTop10Import();
    }
}
