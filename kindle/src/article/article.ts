import Bar from "../common/bar/bar";
import {FeedSource, CatalogueItem, changeValueWithNewObj, Progress} from "../common/common";
import Pagination from "../common/pagination/pagination";

class Article {
    element: HTMLElement;
    bar: Bar;
    pagination: Pagination;

    progress: Progress;

    catalogue: CatalogueItem[] = [];

    content: string;
    contentHeight: number;

    loading: boolean = false;

    constructor() {
        this.element = document.querySelector('.page.article');

        this.pagination = new Pagination({
            root: this.element.querySelector('.content'),
            pageStepCalc: this.pageStepCalc
        });

        this.bar = new Bar({
            element: this.element.querySelector('.bar'),
            pagination: this.pagination
        });

        window.Bind.bindView(this.element.querySelector('.content-inner'), this, 'content', (content: string) => {
            if (!content) {
                return '';
            }

            let html = `
                <style>
                .content-inner *{
                    font-size: ${window.Layout.fontSize}rem !important;
                    line-height: ${window.Layout.lineHeight}rem !important;
                }
                .content-inner p, .content-inner span{
                    text-indent: ${2 * window.Layout.fontSize}rem;
                    display: block;
                }
                </style>
                ${content}
            `;

            window.setTimeout(() => {
                this.pagination.checkPage();
            });
            return html;
        });

        window.Bind.bind(this, 'progress', (newV: any, oldV: any) => {
            if (!oldV) {
                return;
            }
            if (this.progress.pos > this.content.length) {
                return;
            }
        });

        let content: HTMLElement = this.element.querySelector('.content');
        let contentInner: HTMLElement = content.querySelector('.content-inner');
        window.Bind.bindStyle(contentInner, window.Layout, 'fontSize', 'fontSize', (v: any) => `${v}rem`);
        window.Bind.bindStyle(contentInner, window.Layout, 'lineHeight', 'lineHeight', (v: any) => `${v}rem`);
        window.Bind.bindStyle(content, window.Layout, 'height', 'height', (v: any) => {
            if (!this.element.offsetHeight) {
                return '';
            }
            let base = this.element.offsetHeight - (140 + 50); // 基础高度
            // 多出来的一行
            let oo = base % window.Layout.lineHeight;
            if (oo < 10) {
                oo += window.Layout.lineHeight;
            }
            let height = base - oo - 30;
            this.contentHeight = height
            window.setTimeout(() => this.pagination.checkPage());
            return `${height}px`;
        });

        let func = () => {
            // 不在检查feed
            // let current = window.Store.get('current_feed');
            // this.currentBook = window.BookShelf.bookMap[current];
            // if (!this.currentBook) {
            //     if (window.Router.current === 'article') {
            //         window.Router.go('index');
            //     }
            //     return;
            // }
            this.getContent();
        };

        window.Router.cbMap.article = func;
        func();
    }

    pageStepCalc(index: number): number {
        //TODO dev 需要处理步进的问题
        return this.pagination.pageStep * index
    }

    clickTop(): void {
    }

    clickLeft(): void {
        this.pageChange(-1)
    }

    clickRight(): void {
        this.pageChange(1)
    }

    getContent(): void {
        let cb = () => {
        };
        this.getArticle(cb);
    }

    pageChange(num: 1 | -1): void  {
        let target = this.pagination.pageIndex + num;
        if (target < 0 || target >= this.pagination.pageLimit) {
            // TODO 阅读完成直接切换下一文档，暂不支持
            // let index = this.progress.index + num;
            // let pos = num === -1?999999999999:0;// to the end
            // this.progress = changeValueWithNewObj(this.progress, {index: index, title: this.catalogue[index].title, time: new Date().getTime(), pos: pos});
            // this.getContent();
        } else {
            this.pagination.setPage(target);
            this.getPagePos(target);
        }
    }

    getPagePos(target: number): void {
        let top = target * this.pagination.pageStep;
        let ps = this.element.querySelectorAll('.content-inner p');
        let str = '';
        for (let i = 0; i < ps.length; i++) {
            if ((ps[i] as HTMLElement).offsetTop >= top) {
                str = ps[i].innerHTML;
                break;
            }
        }
        let pos = this.content.indexOf(str);
    }

    getArticle(cb?: Function): void {
        let articleId = window.Store.get('current_article');
        if (!articleId) {
            return;
        }
        if (this.loading === true) {
            window.Message.add({content: '正在加载文章内容'});
            return;
        }
        this.loading = true;
        this.content = '<div style="text-align: center;">加载中...</div>'
        window.Api.getArticle(articleId, 1, {
            success: (res: any) => {
                this.loading = false;
                this.content = res.data.content;
                this.pagination.setPage(1);
                cb && cb();
            },
            error: (err: any) => {
                this.loading = false;
            }
        });
    }
};

export default Article;
