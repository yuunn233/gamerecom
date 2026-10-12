package com.example.gamerecom.common;

import com.example.gamerecom.dao.Comment_infoDao;
import com.example.gamerecom.entity.Account;
import com.example.gamerecom.entity.Cart_info;
import com.example.gamerecom.entity.Comment_info;
import com.example.gamerecom.entity.Games_info;
import com.example.gamerecom.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationUtils {
    private static final Logger log = LoggerFactory.getLogger(RecommendationUtils.class);
    @Resource
    private Comment_infoDao commentInfoDao;
    @Resource
    private  GamesInfoService gamesInfoService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private AdminInfoService adminInfoService;

    @Resource
    private AccountService accountService;
    @Resource
    private CartInfoService cartInfoService;

    //随机推荐
    public static List<Games_info> random(List<Games_info> list, int num) {
        if (list == null || list.isEmpty() || num <= 0) {
            System.err.println("输入列表为空或数量无效");
            return Collections.emptyList();
        }
        List<Games_info> shuffledList = new ArrayList<>(list);
        Collections.shuffle(shuffledList); // 打乱列表顺序
        return shuffledList.subList(0, Math.min(num, shuffledList.size()));
    }

    //去重
    private static List<Games_info> deduplicate(List<Games_info> list, List<Games_info> gamesInfos) {
        //将随即推荐与ubcf中的去重
        Set<Games_info> set = new HashSet<>();
        for (Games_info gamesInfo:list){
            set.add(gamesInfo);
        }
        for (Games_info gamesInfo:gamesInfos){
            set.remove(gamesInfo);
        }
        List<Games_info> gamesInfoList = new ArrayList<>(set);
        return gamesInfoList;
    }

    // 基于内容的推荐
    public List<Games_info> contentBasedRecommendation(Long userId, List<Games_info> allGames, int limit) {
        System.out.println("CONTENT 开始执行基于内容的推荐，用户 ID: " + userId);

        // 获取用户收藏的游戏ID
        List<Cart_info> cartInfos = cartInfoService.getCartInfoByUserId(userId);
        Set<Long> favoriteGameIds = cartInfos.stream()
                .map(Cart_info::getGamesId)
                .collect(Collectors.toSet());

        if (favoriteGameIds.isEmpty()) {
            System.out.println("CONTENT 用户没有收藏任何游戏，无法生成推荐");
            return Collections.emptyList();
        }

        System.out.println("CONTENT 用户收藏的游戏ID: " + favoriteGameIds);

        // 获取用户收藏的游戏列表
        List<Games_info> favoriteGames = gamesInfoService.list().stream()
                .filter(game -> favoriteGameIds.contains(game.getId()))
                .collect(Collectors.toList());

        // 计算每个游戏与收藏游戏的相似度
        Map<Games_info, Double> similarityScores = new HashMap<>();
        for (Games_info game : allGames) {
            if (favoriteGameIds.contains(game.getId())) {
                continue; // 跳过用户已经收藏的游戏
            }

            // 计算与每个收藏游戏的相似度，并取平均值
            double totalSimilarity = 0.0;
            for (Games_info favoriteGame : favoriteGames) {
                double similarity = calculateSimilarity(game, favoriteGame);
                totalSimilarity += similarity;
                System.out.println("CONTENT 游戏 " + game.getId() + " 与收藏游戏 " + favoriteGame.getId() + " 的相似度: " + similarity);
            }
            double averageSimilarity = totalSimilarity / favoriteGames.size();
            similarityScores.put(game, averageSimilarity);
            System.out.println("CONTENT 游戏 " + game.getId() + " 的平均相似度: " + averageSimilarity);
        }

        // 按相似度排序并返回前 limit 个游戏
        List<Games_info> recommendedGames = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Games_info, Double>comparingByValue().reversed())
                .filter(entry -> entry.getValue() > 0.3) // 调整过滤条件
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());

        log.info("CONTENT基于内容 推荐结果：{}", recommendedGames);
        return recommendedGames;
    }


    // 基于点击的内容的推荐
    public static List<Games_info> contentBasedRecommendations(List<Games_info> allGames, Games_info targetGame, int limit) {
        System.out.println("开始执行基于内容的推荐，目标游戏 ID: " + targetGame.getId());
        // 计算每个游戏与目标游戏的相似度
        Map<Games_info, Double> similarityScores = new HashMap<>();
        for (Games_info game : allGames) {
            if (game.getId().equals(targetGame.getId())) {
                continue;
            }
            double similarity = calculateSimilarity(game, targetGame);
            similarityScores.put(game, similarity);
            System.out.println("游戏 " + game.getId() + " 与目标游戏的相似度: " + similarity);
        }

        // 按相似度排序并返回前 limit 个游戏
        List<Games_info> recommendedGames = similarityScores.entrySet().stream()
                .sorted(Map.Entry.<Games_info, Double>comparingByValue().reversed())
                .filter(entry -> entry.getValue() > 0.8) // 过滤相似度较低的游戏
                .map(Map.Entry::getKey)
                .limit(limit)
                .collect(Collectors.toList());

        log.info("CONTENT 点击的内容 推荐结果：{}", recommendedGames);
        return recommendedGames;
    }

    //计算内容相似度
    private static double calculateSimilarity(Games_info game1, Games_info game2) {
        double similarity = 0.0;

        // 类型相似度（类型相同加0.5分）
        if (game1.getTypeId().equals(game2.getTypeId())) {
            similarity += 0.5;
        }
        // 热度相似度（根据热度比例计算相似度，最高加0.3分）
        if (game1.getHot() != null && game2.getHot() != null) {
            similarity += Math.min(game1.getHot(), game2.getHot()) / Math.max(game1.getHot(), game2.getHot()) * 0.3;
        }
        // 推荐标志相似度（两个游戏都被推荐加0.2分）
        if ("是".equals(game1.getIsRecommend()) && "是".equals(game2.getIsRecommend())) {
            similarity += 0.2;
        }
        // 评分相似度（评分越接近，相似度越高，最高加0.2分）
        if (game1.getScores() != null && game2.getScores() != null) {
            double scoreSimilarity = 1 - Math.abs(game1.getScores() - game2.getScores()) / 10.0; // 评分范围是0-10
            similarity += Math.max(scoreSimilarity, 0) * 0.2;
        }
        // 价格相似度（价格越接近，相似度越高，最高加0.2分）
        if (game1.getPrice() != null && game2.getPrice() != null) {
            double priceDiff = Math.abs(game1.getPrice() - game2.getPrice());
            double priceSimilarity = 1 - (priceDiff / Math.max(game1.getPrice(), game2.getPrice()));
            similarity += Math.max(priceSimilarity, 0) * 0.2;
        }
        // 游戏厂商相似度（厂商相同加0.2分）
        if (game1.getMaker() != null && game2.getMaker() != null && game1.getMaker().equals(game2.getMaker())) {
            similarity += 0.2;
        }
        return similarity;
    }

    //物品协同过滤
    public List<Games_info> IBCF(List<Comment_info> userComments, int limit) {
        System.out.println("开始执行 IBCF 推荐");
        if (userComments == null || userComments.isEmpty()) {
            System.err.println("用户没有评论过游戏，无法生成推荐");
            return Collections.emptyList();
        }

        // 获取用户评论过的游戏ID
        Set<Long> excludeGameIds = userComments.stream()
                .map(comment -> comment.getGamesId()) // 提取每个评分记录中的游戏ID
                .collect(Collectors.toSet()); // 将游戏ID存储到集合中

        // 存储每个候选游戏的预测评分
        Map<Games_info, Double> gameScores = new HashMap<>();

        // 获取用户评论过的游戏及其评分
        Map<Long, Double> userGameRatings = userComments.stream()
                .collect(Collectors.toMap(
                        comment -> comment.getGamesId(),
                        comment -> Double.parseDouble(comment.getRating()),
                        (oldValue, newValue) -> newValue // 如果有重复键，保留最新的评分
                ));

        // 对用户评论过的每个游戏，找出相似的游戏
        for (Long gameId : userGameRatings.keySet()) {
            Games_info ratedGame = gamesInfoService.findById(gameId);
            System.out.println(ratedGame);
            if (ratedGame == null) continue;

            // 用户对当前游戏的评分
            double userRating = userGameRatings.get(gameId);
            System.out.println("IBCF 处理游戏：ID = " + ratedGame.getId());

            // 找出与当前游戏相似的游戏
            Map<Games_info, Double> similarGames = findSimilarGames(ratedGame,excludeGameIds);

            // 为每个相似游戏计算加权评分
            for (Map.Entry<Games_info, Double> entry : similarGames.entrySet()) {
                Games_info similarGame = entry.getKey();
                double similarity = entry.getValue();
                System.out.println("IBCF 找到相似游戏：ID = " + similarGame.getId() + "，相似度：" + similarity);

                // 累加相似度和评分的乘积
                gameScores.merge(similarGame, similarity * userRating, Double::sum);
                System.out.println("IBCF 游戏 " + similarGame.getId() + " 的预测评分累加：" + similarity * userRating);
            }
        }



        List<Games_info> recommendedGames = gameScores.entrySet().stream()
                .sorted(Map.Entry.<Games_info, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());

        log.info("IBCF 推荐结果：{}", recommendedGames);
        return recommendedGames;
    }

    // 找出与指定游戏相似的游戏 IBCF
    private Map<Games_info, Double> findSimilarGames(Games_info targetGame, Set<Long> excludeGameIds) {
        Map<Games_info, Double> similarityScores = new HashMap<>();

        // 获取当前游戏的所有评分
        List<Comment_info> gameRatings = commentInfoDao.getRatingByGameId(targetGame.getId());
        System.out.println(gameRatings);
        if (gameRatings == null || gameRatings.isEmpty()) {
            return similarityScores; // 如果没有评分记录，直接返回空结果
        }


        // 获取给当前游戏评分的用户ID
        Set<Long> ratedUserIds = gameRatings.stream()
                .map(rating -> rating.getUserId())
                .collect(Collectors.toSet());

        // 找出这些用户评分过的其他游戏
        List<Comment_info> otherRatings = commentInfoDao.getCommentInfoByUserIdIn(ratedUserIds);
        if (otherRatings == null || otherRatings.isEmpty()) {
            return similarityScores; // 如果没有其他评分记录，直接返回空结果
        }
        System.out.println(otherRatings);

        // 按游戏ID分组评分
        Map<Long, List<Comment_info>> ratingsByGame = otherRatings.stream()
                .filter(rating -> !rating.getGamesId().equals(targetGame.getId()))
                .filter(rating -> !excludeGameIds.contains(rating.getGamesId()))
                .collect(Collectors.groupingBy(rating -> rating.getGamesId()));

        // 计算每个候选游戏与当前游戏的相似度
        for (Map.Entry<Long, List<Comment_info>> entry : ratingsByGame.entrySet()) {
            Long candidateGameId = entry.getKey();
            List<Comment_info> candidateRatings = entry.getValue();


            Map<Long, Double> gameRatingsByUser = gameRatings.stream()
                    .collect(Collectors.toMap(
                            rating -> rating.getUserId(),
                            rating -> Double.parseDouble(rating.getRating()),
                            (oldValue, newValue) -> newValue // 如果有重复键，保留最新的评分
                    ));

            Map<Long, Double> candidateRatingsByUser = candidateRatings.stream()
                    .collect(Collectors.toMap(
                            rating -> rating.getUserId(),
                            rating -> Double.parseDouble(rating.getRating()),
                            (oldValue, newValue) -> newValue // 如果有重复键，保留最新的评分
                    ));

            // 找出两个游戏的共同评分用户
            Set<Long> commonUserIds = new HashSet<>(gameRatingsByUser.keySet());
            commonUserIds.retainAll(candidateRatingsByUser.keySet());

            // 至少需要有2个共同评分用户
            if (commonUserIds.size() < 2) {
                continue;
            }

            // 计算余弦相似度
            double similarity = calculateCosineSimilarity(
                    gameRatingsByUser, candidateRatingsByUser, commonUserIds);

            // 如果相似度大于阈值，添加到结果中
            if (similarity > 0.9) {
                Games_info candidateGame = gamesInfoService.findById(candidateGameId);
                if (candidateGame != null) {
                    similarityScores.put(candidateGame, similarity);
                }
            }
        }

        return similarityScores;
    }

    // 计算余弦相似度-1~1
    private static double calculateCosineSimilarity(
            Map<Long, Double> ratings1,
            Map<Long, Double> ratings2,
            Set<Long> commonUserIds) {

        if (commonUserIds.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;//点积
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (Long userId : commonUserIds) {
            double val1 = ratings1.getOrDefault(userId, 0.0);
            double val2 = ratings2.getOrDefault(userId, 0.0);

            dotProduct += val1 * val2;
            norm1 += val1 * val1;
            norm2 += val2 * val2;//模长平方
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    //用户协同过滤
    public List<Games_info> UBCF(Long userId, List<Comment_info> userComments, int limit) {
        System.out.println("开始执行 UBCF 推荐，用户 ID: " + userId);

        // 获取用户评论过的游戏ID
        Set<Long> excludeGameIds = userComments.stream()
                .map(comment -> comment.getGamesId()) // 提取每个评分记录中的游戏ID
                .collect(Collectors.toSet()); // 将游戏ID存储到集合中

        // 获取所有用户
        List<Account> allUsers = accountService.findAll();

        // 当前用户的评分
        Map<Long, Double> userRatings = commentInfoDao.getCommentInfoByUserId(userId).stream()
                .collect(Collectors.toMap(
                        rating -> rating.getGamesId(),
                        rating -> Double.parseDouble(rating.getRating()),
                        (oldValue, newValue) -> newValue // 如果有重复键，保留最新的评分
                ));
        System.out.println( "UBCF 用户 " + userId + " 的评分记录：" + userRatings);

        // 存储相似用户及其相似度
        Map<Account, Double> similarUsers = new HashMap<>();

        // 计算当前用户与其他用户的相似度
        for (Account otherUser : allUsers) {
            if (otherUser.getId().equals(userId)) {
                continue;
            }

            // 获取其他用户的评分
            Map<Long, Double> otherUserRatings = commentInfoDao.findByUser(otherUser.getId()).stream()
                    .collect(Collectors.toMap(
                            rating -> rating.getGamesId(),
                            rating -> Double.parseDouble(rating.getRating()),
                            (oldValue, newValue) -> newValue // 如果有重复键，保留最新的评分
                    ));

            // 找出两个用户的共同评分游戏
            Set<Long> commonGameIds = new HashSet<>(userRatings.keySet());
            commonGameIds.retainAll(otherUserRatings.keySet());

            // 至少需要有2个共同评分游戏
            if (commonGameIds.size() < 2) {
                continue;
            }

            // 计算余弦相似度
            double similarity = calculateCosineSimilarity(
                    userRatings, otherUserRatings, commonGameIds);

            // 如果相似度大于阈值，添加到相似用户列表
            if (similarity > 0.9) {
                similarUsers.put(otherUser, similarity);
                System.out.println("UBCF 找到相似用户： " +  otherUser.getId() + " 相似度：" + similarity);
            }
        }

        // 如果没有找到相似用户，返回空列表
        if (similarUsers.isEmpty()) {
            log.warn("未找到与用户 {} 相似的用户，无法生成推荐", userId);
            return Collections.emptyList();
        }

        // 存储候选游戏的预测评分
        Map<Games_info, Double> gameScores = new HashMap<>();
        Map<Games_info, Double> similaritySum = new HashMap<>();

        // 遍历相似用户，找出他们评分高的游戏
        for (Map.Entry<Account, Double> entry : similarUsers.entrySet()) {
            Account similarUser = entry.getKey();
            double similarity = entry.getValue();

            // 获取相似用户的评分
            List<Comment_info> similarUserRatings = commentInfoDao.findByUser(similarUser.getId());
            System.out.println("UBCF 获取用户评论数据： " +  userComments);
            if (userComments == null || userComments.isEmpty()) {
                log.warn("用户 {} 没有评论过游戏，无法生成推荐", userId);
                return Collections.emptyList();
            }

            for (Comment_info rating : similarUserRatings) {
//                Games_info game = gamesInfoService.findById(rating.getGamesId());
                // 获取游戏对象
                Games_info game = gamesInfoService.findById(rating.getGamesId());
                if (game == null) {
                    // 如果游戏对象为 null，跳过当前循环
                    continue;
                }
                System.out.println("UBCF 获取游戏对象： " +  game);
                if (game == null) {
                    log.warn("未找到游戏 ID 为 {} 的游戏记录", rating.getGamesId());
                    continue;
                }

                // 排除用户已评分的游戏
                if (excludeGameIds.contains(game.getId())) {
                    continue;
                }

                // 高于平均评分的评分才会被考虑
                try {
                    double ratingValue = Double.parseDouble(rating.getRating());
                    if (ratingValue >= 2.0) {
                        gameScores.merge(game, similarity * ratingValue, Double::sum);
                        similaritySum.merge(game, similarity, Double::sum);
                        System.out.println("UBCF 游戏 " + game.getId() + " 的预测评分累加：" + similarity * ratingValue);
                    }
                } catch (NumberFormatException e) {
                    // 处理转换失败的情况，例如记录日志或提示用户
//                    System.err.println("评分转换失败: " + rating.getRating());
                    log.error("UBCF 评分转换失败: {}", rating.getRating());
                }
            }
        }

        // 计算最终预测评分
        Map<Games_info, Double> normalizedScores = new HashMap<>();
        for (Map.Entry<Games_info, Double> entry : gameScores.entrySet()) {
            Games_info game = entry.getKey();
            Double totalScore = entry.getValue();
            Double totalSimilarity = similaritySum.get(game);

            if (totalSimilarity > 0) {
                normalizedScores.put(game, totalScore / totalSimilarity);
                System.out.println("UBCF 游戏 " + game.getId() + " 的最终预测评分：" + totalScore / totalSimilarity);
            }
        }

        // 排序并返回前12个推荐游戏
        List<Games_info> recommendedGames = normalizedScores.entrySet().stream()
                .sorted(Map.Entry.<Games_info, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());

        log.info("UBCF 推荐结果：{}", recommendedGames);
        return recommendedGames;
    }



    // 计算收藏游戏的特征权重
    private Map<String, Double> calculateFeatureWeights(Set<Long> favoriteGameIds) {
        Map<String, Double> featureWeights = new HashMap<>();
        List<Games_info> favoriteGames = gamesInfoService.list().stream()
                .filter(game -> favoriteGameIds.contains(game.getId()))
                .collect(Collectors.toList());

        // 初始化特征权重
        featureWeights.put("typeId", 0.0);
        featureWeights.put("hot", 0.0);
        featureWeights.put("isRecommend", 0.0);
        featureWeights.put("scores", 0.0);
        featureWeights.put("price", 0.0);
        featureWeights.put("maker", 0.0);

        // 计算每个特征的权重
        for (Games_info game : favoriteGames) {
            featureWeights.put("typeId", featureWeights.get("typeId") + game.getTypeId());
            featureWeights.put("hot", featureWeights.get("hot") + game.getHot());
            featureWeights.put("isRecommend", featureWeights.get("isRecommend") + ("是".equals(game.getIsRecommend()) ? 1 : 0));
            featureWeights.put("scores", featureWeights.get("scores") + game.getScores());
            featureWeights.put("price", featureWeights.get("price") + game.getPrice());
            featureWeights.put("maker", featureWeights.get("maker") + (game.getMaker() != null ? 1 : 0));
        }

        // 归一化权重
        for (String feature : featureWeights.keySet()) {
            featureWeights.put(feature, featureWeights.get(feature) / favoriteGames.size());
        }

        System.out.println("content 特征权重: " + featureWeights);
        return featureWeights;

    }

    // 计算游戏与收藏游戏的相似度
    private double calculateSimilarity(Games_info game, Map<String, Double> featureWeights) {
        double similarity = 0.0;

        // 类型相似度
        if (featureWeights.get("typeId") > 0) {
            similarity += featureWeights.get("typeId") * (game.getTypeId() / featureWeights.get("typeId"));
        }

        // 热度相似度
        if (featureWeights.get("hot") > 0) {
            similarity += featureWeights.get("hot") * (game.getHot() / featureWeights.get("hot"));
        }

        // 推荐标志相似度
        if (featureWeights.get("isRecommend") > 0) {
            similarity += featureWeights.get("isRecommend") * (("是".equals(game.getIsRecommend()) ? 1 : 0) / featureWeights.get("isRecommend"));
        }

        // 评分相似度
        if (featureWeights.get("scores") > 0) {
            similarity += featureWeights.get("scores") * (game.getScores() / featureWeights.get("scores"));
        }

        // 价格相似度
        if (featureWeights.get("price") > 0) {
            similarity += featureWeights.get("price") * (game.getPrice() / featureWeights.get("price"));
        }

        // 厂商相似度
        if (featureWeights.get("maker") > 0) {
            similarity += featureWeights.get("maker") * ((game.getMaker() != null ? 1 : 0) / featureWeights.get("maker"));
        }

        // 归一化相似度
        similarity /= featureWeights.values().stream().filter(weight -> weight > 0).mapToDouble(Double::doubleValue).sum();

        System.out.println("相似度: " + similarity);
        return similarity;
    }

}
