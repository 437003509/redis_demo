package com.example.redis_demo;

import com.example.redis_demo.Bean.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class RedisDemoApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;

    private static final int ONE_WEEK_IN_SECONDS = 7 * 86400;
    private static final int VOTE_SCORE = 432;
    private static final int ARTICLES_PER_PAGE = 25;

    @Test
    void contextLoads() {
        //1 插入
        redisTemplate.opsForValue().set("key", "value");
        //2 查询
        Object result = redisTemplate.opsForValue().get("key");
        System.out.println(result);
        //3 删除
//        redisTemplate.delete("key");
    }

//    String articleId = String.valueOf(conn.incr("article:"));
//
//    String voted = "voted:" + articleId;
//        conn.sadd(voted, user);
//        conn.expire(voted, ONE_WEEK_IN_SECONDS);
//
//    long now = System.currentTimeMillis() / 1000;
//    String article = "article:" + articleId;
//    HashMap<String,String> articleData = new HashMap<String,String>();
//        articleData.put("title", title);
//        articleData.put("link", link);
//        articleData.put("user", user);
//        articleData.put("now", String.valueOf(now));
//        articleData.put("votes", "1");
//        conn.hmset(article, articleData);
//        conn.zadd("score:", now + VOTE_SCORE, article);
//        conn.zadd("time:", now, article);
//

    /**
     * 发布文章
     *
     * @param articleBean
     * @return
     */
    String postArticle(Article articleBean) {
        String articleId = String.valueOf(redisTemplate.opsForValue().increment("article:"));//自增

        String voted = "voted:" + articleId;
        redisTemplate.opsForSet().add(voted, "user:" + articleBean.getPoster());   //集合：存储每个文章投票的用户
        redisTemplate.expire(voted, ONE_WEEK_IN_SECONDS, TimeUnit.SECONDS);  //设置过期时间，满一周后自动删除这个集合

        long now = System.currentTimeMillis() / 1000;
        String article = "article:" + articleId;
        HashMap<String, String> articleData = new HashMap<>();   //存储文章数据
        //初始化数据
        articleData.put("title", articleBean.getTitle());
        articleData.put("link", articleBean.getLink());
        articleData.put("user", "user:" + articleBean.getPoster());
        articleData.put("now", String.valueOf(now));
        articleData.put("votes", "1");

        redisTemplate.opsForHash().putAll(article, articleData);    //散列：文章详情
        redisTemplate.opsForZSet().add("score", article, now + VOTE_SCORE);     //第一个有序集合成员为文章id，分值为评分，初始值为发布时间+初始评分
        redisTemplate.opsForZSet().add("time", article, now);   //第二个有序集合成员为文章id，分值为发布时间

        return articleId;

    }

    @Test
    void testPostArticle() {
        Article article = new Article("Redis实战3", "www.baidu.com", "3");
        System.out.println("成功发布文章：" + postArticle(article));
    }


//    public List<Map<String,String>> getArticles(Jedis conn, int page, String order) {
//        int start = (page - 1) * ARTICLES_PER_PAGE;
//        int end = start + ARTICLES_PER_PAGE - 1;
//
//        Set<String> ids = conn.zrevrange(order, start, end);
//        List<Map<String,String>> articles = new ArrayList<Map<String,String>>();
//        for (String id : ids){
//            Map<String,String> articleData = conn.hgetAll(id);
//            articleData.put("id", id);
//            articles.add(articleData);
//        }
//
//        return articles;
//    }

    /**
     * 获取文章
     * @param page 当前页数
     * @param order 排序
     * @return
     */
    public List<Map<String,String>> getArticles(int page, String order) {
        //分页
        int start = (page - 1) * ARTICLES_PER_PAGE;
        int end = start + ARTICLES_PER_PAGE - 1;

        Set<String> ids = redisTemplate.opsForZSet().reverseRange(order,start,end); //按分值从大到小排列（range默认是从小到大）
        System.out.println(ids.toArray().length);
         List<Map<String, String>> articles = new ArrayList<>();
        for (String id : ids
             ) {
            System.out.println("id:"+id);
            Map<String, String> articleData = redisTemplate.opsForHash().entries(id);//通过获取所有信息
            articleData.put("id",id);
            articles.add(articleData);
        }

        return articles;
    }

    @Test
    void testGetArticles(){
        List<Map<String, String>> articles = getArticles(1, "score");
    }



//    public void articleVote(Jedis conn, String user, String article) {
//        long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
//        if (conn.zscore("time:", article) < cutoff){
//            return;
//        }
//
//        String articleId = article.substring(article.indexOf(':') + 1);
//        if (conn.sadd("voted:" + articleId, user) == 1) {
//            conn.zincrby("score:", VOTE_SCORE, article);
//            conn.hincrBy(article, "votes", 1l);
//        }
//    }

    /**
     * 投票
     * @param user
     * @param article
     */
    void articleVote(String user,String article){
        long cutoff = (System.currentTimeMillis() / 1000) - ONE_WEEK_IN_SECONDS;
        //判断是否过期
        if(redisTemplate.opsForZSet().score("time",article) < cutoff){
            return;
        }
        String articleId = article.substring(article.indexOf(":") + 1);
        if(redisTemplate.opsForSet().add("voted:"+articleId,user)==1){
            redisTemplate.opsForZSet().incrementScore("score",article,VOTE_SCORE); //评分增加
            redisTemplate.opsForHash().increment(article,"votes",1);
        }

    }

    @Test
    void testrticleVoteA(){
        articleVote("user:3","article:1");
    }




}
