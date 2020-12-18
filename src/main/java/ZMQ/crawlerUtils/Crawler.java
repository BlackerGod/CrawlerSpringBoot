package ZMQ.crawlerUtils;

import ZMQ.mapper.ProjectTableMapper;
import ZMQ.model.Project;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class Crawler implements ApplicationRunner {
    static class CrawlerTask implements Runnable{

        private Project project;
        private Crawler Crawler;

        public CrawlerTask(Project project, Crawler threadCrawler) {
            this.project = project;
            this.Crawler = threadCrawler;
        }

        @Override
        public void run() {
            //1.调用API获取项目数据
            //2.解析项目数据
            try {
                System.out.println("craw"+project.getName()+"...");
                String repoName = Crawler.getRepoName(project.getUrl());
                String jsonString = Crawler.getRepoInfo(repoName);
                Crawler.parseRepoInfo(jsonString,project);
            } catch (IOException e) {
                throw new RuntimeException("插入失败"+project.getUrl());
            }

        }
    }

    private  OkHttpClient okHttpClient = new OkHttpClient();

    private HashSet<String> htmlBlackList = new HashSet<>();

    private String AuthTOKEN = "496a2e36d5543f28642641a6e92b32e83e4039fa";

    private Gson gson = new GsonBuilder().create();

    private static final int THREADCOUNT = 10;

    @Autowired
    private ProjectTableMapper projectTableMapper;

    {
        htmlBlackList.add("https://github.com/events");
        htmlBlackList.add("https://github.community");
        htmlBlackList.add("https://github.com/about");
        htmlBlackList.add("https://github.com/contact");
        htmlBlackList.add("https://github.com/pricing");
    }


    @Override
    @Scheduled(cron = "0 0 0 * * * *")
    public void run(ApplicationArguments args) throws Exception {
        Crawler crawler = new Crawler();
        //获取我们要抓的页面
        String html = "";
        boolean flag = false;
        while (!flag) {
            try {
                html = crawler.getUrl("https://github.com/akullpp/awesome-java/blob/master/README.md");
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //从这个页面获得每一个项目保存到result；
        List<Project> result = crawler.parseProjectList(html);
        ExecutorService executorService = Executors.newFixedThreadPool(THREADCOUNT);
        List<Future<?>> taskResults = new ArrayList<>();
        for (Project project: result){
            Future<?> taskResult = executorService.submit(new CrawlerTask(project,crawler));
            taskResults.add(taskResult);
        }
        for (Future<?> taskResult : taskResults){
            try {
                taskResult.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //所有任务都执行完毕了,关闭线程池,回收资源
        executorService.shutdown();

        System.out.println("开始插入数据库");
        int counts = 0;
        for (Project project: result) {
            counts += projectTableMapper.insert(project);
        }
        System.out.printf("插入%d条记录",counts);
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    public  String getUrl(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        if(!response.isSuccessful()){
            System.out.println("请求失败");
            return null;
        }
        return response.body().string();
    }

    public  List<Project> parseProjectList(String html){
        ArrayList<Project> result = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByTag("li");
        String date = DateUtil.toDay();
        for (Element li : elements){
            Elements allLink = li.getElementsByTag("a");
            if(allLink.size() == 0){
                continue;
            }
            Element link = allLink.get(0);
            String url = link.attr("href");
            if(!url.startsWith("https://github.com")){
                continue;
            }
            if(htmlBlackList.contains(url)){
                continue;
            }
            Project project = new Project();
            project.setName(link.text());
            project.setUrl(link.attr("href"));
            project.setDescrption(li.text());
            project.setDate(date);
            result.add(project);
        }
        return result;

    }

    /**
     * respoName仓库名/项目名
     * 调用Github的API获取指定项目仓库的信息；
     * */
    public String getRepoInfo(String respoName) throws IOException {

        //身份认证,把用户名密码加密后，得到一个字符串，把这个字符串放在HTTP header里面
        // String credential = Credentials.basic(username,password);
        String credential = "token " + AuthTOKEN;
        String url = "https://api.github.com/repos/"+respoName;
        Request request = new Request.Builder().url(url).header("Authorization",credential).build();
        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        if(!response.isSuccessful()){
            System.out.println("获取项目失败");
            return null;
        }
        return response.body().string();
    }

    /**
     * 提取出来仓库名字和项目名字
     * */
    public String getRepoName(String url){
        int lastOne = url.lastIndexOf("/");
        int lastTwo = url.lastIndexOf("/",lastOne-1);
        if(lastOne == -1 || lastTwo == -1){
            System.out.println("url不是一个项目的url"+url);
            return null;
        }
        return url.substring(lastTwo+1);
    }

    /**
     * 第一个参数，表示Github获取到的结果;
     * 第二个参数,每个项目;
     * */
    public void parseRepoInfo(String jsonString,Project project){
        Type type = new TypeToken<HashMap<String,Object>>(){}.getType();
        HashMap<String,Object> hashMap = gson.fromJson(jsonString,type);
        project.setStarcount(((Double) hashMap.get("stargazers_count")).intValue());
        project.setForkcount(((Double) hashMap.get("forks_count")).intValue());
        project.setOpenissuecount(((Double) hashMap.get("open_issues_count")).intValue());
    }

}
