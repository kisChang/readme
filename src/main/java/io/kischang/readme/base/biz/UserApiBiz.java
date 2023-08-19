package io.kischang.readme.base.biz;

import io.kischang.readme.app.dao.*;
import io.kischang.readme.app.model.FeedArticle;
import io.kischang.readme.app.model.FeedSource;
import io.kischang.readme.app.model.UserArticle;
import io.kischang.readme.app.model.UserFeeds;
import io.kischang.readme.app.qo.ChangePasswordParam;
import io.kischang.readme.app.qo.LoginRegParam;
import io.kischang.readme.app.utils.AccountUtils;
import io.kischang.readme.app.utils.HttpUtils;
import io.kischang.readme.app.utils.RSSReaderUtil;
import io.kischang.readme.app.utils.ReadmeUtils;
import io.kischang.readme.base.auth.JwtUtils;
import io.kischang.readme.base.bean.R;
import io.kischang.readme.base.exception.BaseException;
import io.kischang.readme.user.dao.UserAccountDao;
import io.kischang.readme.user.model.UserAccount;
import org.apache.james.core.MailAddress;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserApiBiz {

    @Resource
    private FeedSourceDao feedSourceDao;
    @Resource
    private FeedArticleDao feedArticleDao;
    @Resource
    private UserArticleDao userArticleDao;
    @Resource
    private UserFeedsDao userFeedsDao;
    @Resource
    private UserAccountDao userAccountDao;
    @Resource
    private UserTrustMailsDao trustMailsDao;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private AdminApiBiz adminApiBiz;

    @Transactional
    public R<?> addFeedById(long userId, long feedId) {
        UserFeeds find = userFeedsDao.findByFeedIdAndUserId(feedId, userId);
        if (find != null) {
            return R.ok("已添加过，请勿重复操作");
        }
        Optional<FeedSource> fs = feedSourceDao.findById(feedId);
        if (fs.isEmpty()) {
            return R.fail("订阅源未找到，请刷新后重试！");
        }
        find = new UserFeeds();
        find.setUserId(userId);
        find.setFeedId(feedId);
        userFeedsDao.save(find);
        // 找出最近6篇文章，写入记录
        List<FeedArticle> articleList = feedArticleDao.findAllByFeedId(feedId, PageRequest.of(0, 6));
        for (FeedArticle feedArticle : articleList) {
            UserArticle userArticle = userArticleDao.findFirstByUserIdAndArticleIdAndFeedId(userId, feedArticle.getArticleId(), feedId);
            if (userArticle == null) {
                userArticle = new UserArticle();
                userArticle.setUserId(userId);
                userArticle.setArticleId(feedArticle.getArticleId());
                userArticle.setFeedId(feedArticle.getFeedId());
                userArticle.setStatus(0);
                userArticleDao.save(userArticle);
            }
        }


        return R.ok("操作成功！");
    }

    // 收到内容的处理机制
    @Transactional
    public void recvMailWorker(List<MailAddress> recipients, String sendByMail
            , String messageTitle, String messageContent) {
        FeedArticle feedArticle = null;
        for (MailAddress recipient : recipients) {
            if (!"kischang.top".equalsIgnoreCase(recipient.getDomain().asString())) {
                // 非本邮箱，丢弃
                continue;
            }
            UserAccount findUser = userAccountDao.findByUsername(recipient.getLocalPart());
            if (findUser == null) {
                // 不存在的用户，丢弃
                continue;
            }
            Set<String> trustMails = trustMailsDao.findMailsByUserId(findUser.getUserId());
            if (!trustMails.contains(sendByMail)) {
                // 非信任邮箱，丢弃
                continue;
            }

            //3. 处理消息体内容
            if (feedArticle == null) {
                feedArticle = new FeedArticle();
                feedArticle.setSourceType(1);
                feedArticle.setFeedId(0L);
                feedArticle.setSource(sendByMail);
                feedArticle.setTitle(messageTitle);
                feedArticle.setPublishedDate(ReadmeUtils.dateTime());
                feedArticle.setUri(sendByMail + " at " + feedArticle.getPublishedDate());
                // 存储
                feedArticle.setContent(parseMailContent(messageContent));
                if (feedArticle.getContent() == null) {
                    // TODO 链接处理失败,目前的逻辑是丢弃
                    return;
                }
                feedArticle = feedArticleDao.save(feedArticle);
            }

            //4. 加入待阅读清单
            UserArticle userArticle = new UserArticle();
            userArticle.setUserId(findUser.getUserId());
            userArticle.setArticleId(feedArticle.getArticleId());
            userArticle.setFeedId(feedArticle.getFeedId());
            userArticle.setStatus(0);
            userArticleDao.save(userArticle);
        }
    }

    // 匹配第一个URL
    private static final Pattern patter = Pattern.compile("^(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");

    private String parseMailContent(String content) {
        if (content.startsWith("http")) {
            // 作为url处理
            Matcher matcher = patter.matcher(content);
            if (matcher.find()) {
                String match = matcher.group(); // 获取第一个匹配的字符串
                String getContent = HttpUtils.getData(match, StandardCharsets.UTF_8);
                return RSSReaderUtil.parseHtmlContent(getContent);
            }
            return null;
        } else {
            // 作为富文本处理
            return RSSReaderUtil.parseHtmlContent(content);
        }
    }

    @Transactional
    public FeedArticle haveRead(long articleId, long userId) {
        Optional<FeedArticle> article = feedArticleDao.findById(articleId);
        if (article.isPresent()) {
            UserArticle tmp = userArticleDao.findFirstByUserIdAndArticleIdAndFeedId(userId, article.get().getArticleId(), article.get().getFeedId());
            if (tmp == null) {
                tmp = new UserArticle();
                tmp.setUserId(userId);
                tmp.setArticleId(article.get().getArticleId());
                tmp.setFeedId(article.get().getFeedId());
            }
            tmp.setReadDate(ReadmeUtils.dateTime());
            tmp.setStatus(1);
            userArticleDao.save(tmp);
            return article.get();
        } else {
            throw new BaseException(404, "文章未找到");
        }
    }

    @Transactional
    public UserFeeds addFeed(FeedSource feedSource, Long userId) {
        FeedSource old = feedSourceDao.findByUrlLike(feedSource.getUrl());
        if (old == null) {
            // 更新
            feedSource.setLogo("http://readme.kischang.top/static/images/192x192.png");
            old = feedSourceDao.save(feedSource);
        }
        UserFeeds userFeeds = userFeedsDao.findByFeedIdAndUserId(old.getFeedId(), userId);
        if (userFeeds == null) {
            userFeeds = new UserFeeds();
            userFeeds.setFeedId(old.getFeedId());
            userFeeds.setUserId(userId);
            userFeedsDao.save(userFeeds);
        }
        return userFeeds;
    }

    //    @Value("${config.captcha-check}")
    private boolean checkCaptcha = true;

    /**
     * 用户名密码登录
     */
    @Transactional
    public R<UserAccount> login(LoginRegParam param) {
        if (checkCaptcha) {
//            if (param.getCode() == null) {
//                return ApiResult.fail("请输入验证码", null);
//            }
//            // 获取并校验校验码
//            if (redisUtils.getCaptcha(param.getCode()) == null) {
//                //不存在，则证明输入错误或者是已超时
//                return ApiResult.fail("验证码错误或已过期，请重试", null);
//            }
        }
        String username = param.getUsername();
        UserAccount user = userAccountDao.findByUsername(username);
        if (user == null) {
            return R.fail("账号密码错误");
        }
        if (this.notMatchesPassword(user.getPassword(), param.getPassword())) {
            return R.fail("账号密码错误");
        }
        String jwt = JwtUtils.generateToken(username);
        user.setToken(jwt);
        // 返回
        return R.data(user).setMsg("登录成功");
    }

    /**
     * 更新密码
     */
    @Transactional
    public R<UserAccount> updatePassword(String username, ChangePasswordParam param) {
        UserAccount user = userAccountDao.findByUsername(username);
        if (user == null) {
            return R.fail("请先登录");
        }
        if (this.notMatchesPassword(user.getPassword(), param.getOldPassword())) {
            return R.fail("原密码输入错误");
        }
        if (!AccountUtils.checkPassword(param.getNewPassword())) {
            return R.fail("密码不符合规则，必须包含数字、字母、符号中的两种，且大于6位");
        }
        user.setPassword(this.encodePassword(param.getNewPassword()));
        userAccountDao.save(user);
        return R.ok("操作成功");
    }

    private static final Set<String> used = Set.of(
            "username"
            , "user"
            , "root"
            , "admin"
            , "guest"
            , "manager"
            , "123"
            , "123456"
            , "000"
            , "000000"
    );

    @Transactional
    public R<UserAccount> reg(LoginRegParam param) {
        if (checkUsed(param.getUsername())) {
            return R.fail("用户名已占用");
        }
        if (!AccountUtils.checkUsername(param.getUsername())) {
            return R.fail("用户名不符合规则，6-20位字母和数字的组合");
        }
        if (!AccountUtils.checkPassword(param.getPassword())) {
            return R.fail("密码不符合规则，必须包含数字、字母、符号中的两种，且大于6位");
        }

        UserAccount user = new UserAccount();
        user.setUsername(param.getUsername());
        user.setPassword(this.encodePassword(param.getPassword()));
        user.setNickname(param.getNickname());
        user.setReg_time(ReadmeUtils.dateTime());
        user = userAccountDao.save(user);

        String jwt = JwtUtils.generateToken(user.getUsername());
        user.setToken(jwt);
        // 返回
        return R.data(user).setMsg("注册并登录成功");
    }

    public boolean notMatchesPassword(String dbPassword, String inputPassword) {
        return !passwordEncoder.matches(inputPassword, dbPassword);
    }

    public String encodePassword(String password) {
        return password == null ? null : passwordEncoder.encode(password);
    }


    @Transactional
    public R<?> changeUsername(String user, String username) {
        if (username.equals(user)) {
            return R.fail(-1, "未修改！");
        }
        UserAccount loginUser = userAccountDao.findByUsername(user);
        if (loginUser == null) {
            return R.fail(403, "未登录！");
        }
        if (checkUsed(username)) {
            return R.fail(-1, "用户名已占用！");
        }
        loginUser.setUsername(username);
        loginUser = userAccountDao.save(loginUser);
        // 需要重新登录
        String jwt = JwtUtils.generateToken(loginUser.getUsername());
        loginUser.setToken(jwt);
        // 返回
        return R.data(loginUser).setMsg("操作成功");
    }

    private boolean checkUsed(String username) {
        if (used.contains(username.toLowerCase(Locale.ROOT))) {
            return true;
        }
        UserAccount findUse = userAccountDao.findByUsername(username);
        if (findUse != null) {
            return true;
        }
        return false;
    }
}
