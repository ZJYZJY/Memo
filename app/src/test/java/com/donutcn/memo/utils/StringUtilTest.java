package com.donutcn.memo.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * com.donutcn.memo.utils
 * Created by 73958 on 2017/7/29.
 */
public class StringUtilTest {

    private String source = "<article class=\"article\">\n" +
            "\" +\n" +
            "                \"      <p>对于汉字，日本人常怀有一种复杂的情感，既觉得汉字伟大，又觉得汉字难学。在日本，“认识多少汉字”某种程度上可以代表一个人的受教育水平，受过良好教育的人可以读懂较多的汉字。然而，现在的日本年轻人能够读懂的汉字已经越来越少，对于中国人的汉字和语言能力，他们内心也是五味陈杂。</p> \\n\" +\n" +
            "                \"<p>昨天，在2CH论坛上一条标题为“中国人既能看懂日本的‘汉字’，又能说英语，他们这不是最强的吗？”的帖子火了。</p> \\n\" +\n" +
            "                \"<p>那么，中国人到底能不能看懂日本的“汉字”呢？让我们先来做个小测试。</p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/55bbc0c69b634cdfa33d624da133da99.png\" img_width=\\\"602\\\" img_height=\\\"419\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p>上图是2012年发行的日本语能力测试一级（N1）真题中的一篇阅读理解，N1是世界范围内日语考试最高的等级，其官方网站对于N1阅读部分的能力要求是：</p> \\n\" +\n" +
            "                \"<p>能够阅读有关广泛话题的报纸社论、评论等逻辑性稍强、抽象度高的文章，理解文章的构成及内容。</p> \\n\" +\n" +
            "                \"<p>能够阅读有关各类话题的有深度内容的读物，理解上下文及具体的表达意图。</p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/4f5ac67afb5a41fe9c1adbffb9d0ba5e.png\" img_width=\\\"383\\\" img_height=\\\"331\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p>让我们来看一下这篇文章，文中用红线划出的是汉字较多，不懂日语的中国人看了也能知道大概意思的句子：</p> \\n\" +\n" +
            "                \"<p>以前、花見をしている時——<span>以前看花的时候</span></p> \\n\" +\n" +
            "                \"<p>桜の花は本当にきれいな正五角形だね——<span>樱花是正五角形</span></p> \\n\" +\n" +
            "                \"<p>桜の花びらには微妙な色合いや形——<span>樱花有微妙的形状和颜色</span></p> \\n\" +\n" +
            "                \"<p>科学者特有の美意識は——<span>科学家特有的美学意识</span></p> \\n\" +\n" +
            "                \"<p>大胆な抽象化と理想化が必要である——<span>大胆的抽象化和理想化是必要的</span></p> \\n\" +\n" +
            "                \"<p>実際に数学的な意味で完全な正五角形を示す花びらは少ないだろうが——<span>实际上数学意义上完全的表示正五角形的花是少的</span></p> \\n\" +\n" +
            "                \"<p>自然界で正五角形のような対称性を示すためには——<span>自然界表示正五角形的对称性</span></p> \\n\" +\n" +
            "                \"<p>抽象化と理想化によって自然現象は単純に整理でき——<span>抽象化和理想化，单纯地整理自然现象</span></p> \\n\" +\n" +
            "                \"<p>普遍的な法則を見つける助けになる——<span>对看到普遍的法则有帮助</span></p> \\n\" +\n" +
            "                \"<p>以上是我站在不懂日语的中国人的角度，光凭句子中的汉字来理解大概意思的情况下做出的翻译。</p> \\n\" +\n" +
            "                \"<p>通过这种“大概理解”的方式，我们可以简单推测出，文章大概是说樱花很美，是正五角形的，以樱花为例，通过科学的美学意识，用理想化和抽象化的方法，来从自然现象里找到普遍的规律。</p> \\n\" +\n" +
            "                \"<p>然后我再从一个懂日语的人角度来看这篇文章，其实文章的主旨和光凭汉字推测出来的主旨已经八九不离十了。</p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/5003bf4e69ee4a43b2acbb8c3a3d3567.png\" img_width=\\\"300\\\" img_height=\\\"308\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p>另外，在这篇文章里，“正五角形”这个词是需要额外注释，让人更容易明白这是什么意思的。</p> \\n\" +\n" +
            "                \"<p>从这个角度来看，“中国人可以看懂日本汉字”，这一点似乎是不太错的，只要文章里汉字比较多，我们就能大概地理解文章的意思。</p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/af211557fdea4625ae9f90b60d9f67a9.png\" img_width=\\\"419\\\" img_height=\\\"332\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p>那么，日本人怎么看中国人的汉字和语言能力？</p> \\n\" +\n" +
            "                \"<p>首先是惯常的歪楼，他们讨论起了北京人和广东人到底能不能交流的问题……</p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/e76bf255c28f42c48a3abaf63c730050.png\" img_width=\\\"602\\\" img_height=\\\"142\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p>到了70多层才有人来说了句正经话。</p> \\n\" +\n" +
            "                \"<p>繁体字真是让人苦恼啊……</p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/6dd21603821c4380932ccd2fde370b9e.png\" img_width=\\\"602\\\" img_height=\\\"135\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/ee458524d1154ec3943e5036c509cb91.png\" img_width=\\\"602\\\" img_height=\\\"218\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/fbf61c60c6064261af0146cd3bb534c3.png\" img_width=\\\"398\\\" img_height=\\\"277\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p>中文和日本汉字还是存在差异的，用法也有大大小小的不同。</p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/2740fe42b9cf4afc9eb3247fc5eb224f.png\" img_width=\\\"602\\\" img_height=\\\"145\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/17e160dd39c640ab9344d13597389085.png\" img_width=\\\"602\\\" img_height=\\\"180\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p>所以谢和谢谢的区别究竟是什么……</p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/0b8677ba73f44e87a6d2c44665d92997.png\" img_width=\\\"271\\\" img_height=\\\"260\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p>还有人用亲身经历来验证标题的“中国人是最强的”。</p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/aca0f4c0c17b4dcfa43ab6ef7ca4c6b3.png\" img_width=\\\"602\\\" img_height=\\\"182\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p>这位兄弟学中文就是为了有机会跑路？</p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/7eaad8960c5745669f062de616537b8f.png\" img_width=\\\"602\\\" img_height=\\\"212\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p>所以，也教教我们简体字吧！</p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/a9ea549fba05473b8095eb201f9f81c8.png\" img_width=\\\"602\\\" img_height=\\\"196\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p><img src=\"http://5b0988e595225.cdn.sohucs.com/images/20170707/793292cdf82d4749bf13b65fdeb516b4.png\" img_width=\\\"506\\\" img_height=\\\"392\\\" max-width=\\\"600\\\" /></p> \\n\" +\n" +
            "                \"<p>来源：<span>观察者网/肖晟仕</span></p> \\n\" +\n" +
            "                \"<p><span>特别声明：本文转载仅仅是出于传播信息的需要，并不意味着代表本网站观点或证实其内容的真实性；如其他媒体、网站或个人从本网站转载使用，须保留本网站注明的“来源”，并自负版权等法律责任；作者如果不希望被转载或者联系转载稿费等事宜，请与我们接洽。</span></p> \\n\" +\n" +
            "                \"<p>语言不过关被拒？美国EditSprings--专业英语论文润色翻译修改服务专家帮您！</p> \\n\" +\n" +
            "                \"<p><img src=\"http://img.mp.itc.cn/upload/20170627/e14f19cafa844a05a6b0ae83977eb8a3_th.jpg\" max-width=\\\"600\\\" /></p>\\n\" +\n" +
            "                \"</article>\"";

    private List<String> list;

    @Before
    public void setUp() throws Exception {
        list = new ArrayList<>();
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/55bbc0c69b634cdfa33d624da133da99.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/4f5ac67afb5a41fe9c1adbffb9d0ba5e.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/5003bf4e69ee4a43b2acbb8c3a3d3567.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/af211557fdea4625ae9f90b60d9f67a9.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/e76bf255c28f42c48a3abaf63c730050.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/6dd21603821c4380932ccd2fde370b9e.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/ee458524d1154ec3943e5036c509cb91.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/fbf61c60c6064261af0146cd3bb534c3.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/2740fe42b9cf4afc9eb3247fc5eb224f.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/17e160dd39c640ab9344d13597389085.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/0b8677ba73f44e87a6d2c44665d92997.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/aca0f4c0c17b4dcfa43ab6ef7ca4c6b3.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/7eaad8960c5745669f062de616537b8f.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/a9ea549fba05473b8095eb201f9f81c8.png");
        list.add("http://5b0988e595225.cdn.sohucs.com/images/20170707/793292cdf82d4749bf13b65fdeb516b4.png");
        list.add("http://img.mp.itc.cn/upload/20170627/e14f19cafa844a05a6b0ae83977eb8a3_th.jpg");
    }

    @Test
    public void getImgSrcList() throws Exception {
        assertEquals(list, StringUtil.getImgSrcList(source));
    }

}