import BookShelf from './bookshelf/bookshelf';
import Config from './config/config';
import Router from './common/router/router';
import Debugger from './common/debugger/debugger';
import Modal from './common/modal/modal';
import Message from './common/message/message';
import Store from './common/store/store';
import Bind from './common/bind/bind';
import Layout from './common/layout/layout';
import Api from './common/api/api';
import Article from './article/article';
import Catalogue from './catalogue/catalogue';
import Index from "./Index";

const pages: string[] = ['index', 'bookshelf', 'config', 'article', 'catalogue', 'login'];

function init() {
    new Debugger();

    new Bind();

    new Modal();
    new Message();

    new Router(pages);

    new Store();

    new Layout();

    new Api();

    document.querySelector('.global-style').innerHTML = `
        <style>
            .page .content {
                height: ${document.body.offsetHeight - 270}px;
            }
        </style>
    `;

    window.Config = new Config();

    window.Index = new Index();

    window.BookShelf = new BookShelf();

    window.Catalogue = new Catalogue();

    window.Article = new Article();

    // ----- 初始化完成 -----
    if (window.Router.current !== 'login') {
        // 用户功能
        if (window.Store.getToken()) {
            // 已登录
            window.BookShelf.getBookShelf()
        } else {
            window.Router.go('login')
        }
    } else {
        if (window.Store.getToken()) {
            // 已登录,自动跳转首页
            window.Router.go('index')
        }
    }

    // 调用
    window.Router.cbMap.login = window.Config.getLoginQr
    window.Router.callCb()
}

window.init = init;

window.ondblclick = function (event: Event) {
    event.preventDefault();
    return false;
}
