import Bar from "../common/bar/bar";
import { FeedSource, CatalogueItem, PageInfo, getSpecialParent, Progress } from "../common/common";
import Pagination from "../common/pagination/pagination";

class Catalogue {
    element: HTMLElement;
    bar: Bar;
    pagination: Pagination;

    currentBook: FeedSource;
    progress: Progress;

    list: CatalogueItem[] = [];
    pageInfo: PageInfo;
    pageList: CatalogueItem[] = [];

    oo: number = 10;

    loading: boolean = false;

    constructor() {
        this.element = document.querySelector('.page.catalogue');

        this.pagination = new Pagination({
            root: this.element.querySelector('.content'),
            fake: true,
            pageChange: (index: number) => {
                this.getCatalogue(index)
            }
        });

        this.bar = new Bar({
            element: this.element.querySelector('.bar'),
            pagination: this.pagination
        });

        const current: HTMLElement = this.element.querySelector('.current-info');
        window.Bind.bind(this, 'list', (list: CatalogueItem[]) => {
            this.pagination.pageIndex = this.pageInfo.number
            this.pagination.pageLimit = this.pageInfo.totalPages
            this.pageList = list
        });
        window.Bind.bindView(this.element.querySelector('.article-list'), this, 'pageList', (list: CatalogueItem[]) => {
            let html = `
                <style>.article-item {line-height: 80px;}</style>
            `;
            list.forEach((article) => {
                let haveread = article.status == 1 ? 'haveread' : '';
                let star = article.status == 2 ? 'star' : '';
                html += `<div class="article-item ${haveread} ${star}" key="${article.index}" id="${article.articleId}">
                        <span class="title">${article.title} </span>
                        <span class="date">at ${article.publishedDate}</span>
                        </div>`;
            });
            return html;
        });

        window.Bind.bindView(current, this, 'currentBook', () => {
            return `${this.currentBook?.name}`;
        });

        let func = () => {
            this.checkCurrent();

            this.checkHeight();
        };

        window.Router.cbMap.catalogue = func;
        func();
    }

    checkCurrent(): void {
        this.currentBook = window.BookShelf.bookMap[window.Store.get('current_feed')];

        this.getCatalogue(this.pageInfo ? this.pageInfo.number : 0, true);
    }

    checkHeight(): void {
        let height = this.element.offsetHeight - 230 - 20;
        let oo = height % 80;
        if (oo < 10) {
            oo += 80;
        }
        this.oo = oo;
        const current: HTMLElement = this.element.querySelector('.current-info');
        const content: HTMLElement = this.element.querySelector('.content');
        current.style.height = `${oo}px`;
        current.style.lineHeight = `${oo}px`;
        content.style.height = `${height - oo}px`;
    }


    getCatalogue(toPage: number = 0, force: boolean = false): void {
        if (!this.currentBook || !this.currentBook.feedId) {
            return;
        }
        if (this.loading === true) {
            window.Message.add({content: '正在加载目录数据'});
            return;
        }
        this.loading = true;
        if (this.pageInfo && this.pageInfo.number == toPage && !force) {
            this.loading = false;
            return;
        }
        window.Api.getCatalogue(this.currentBook.feedId, toPage, {
            success: (res: any) => {
                this.loading = false;
                this.pageInfo = res.data
                this.list = res.data.content.map((v: any) => {
                    return {
                        index: v.articleId,
                        articleId: v.articleId,
                        publishedDate: v.publishedDate,
                        status: v.status,
                        title: v.title
                    };
                });
            },
            error: (err: any) => {
                this.loading = false;
            }
        });
    }

    clickItem(event: Event): void {
        let item = getSpecialParent((event.target || event.srcElement) as HTMLElement, (ele: HTMLElement) => {
            return ele.classList.contains('article-item');
        });
        let articleId = item.getAttribute('id');
        window.Store.set('current_article', articleId);
        window.setTimeout(() => {
            window.Router.go('article');
        });
    }

    cache(): void {
        window.Modal.add({
            content: `
                <style>
                    .modal-content .button {
                        line-height: 60px;
                        padding: 20px;
                        width: 40%;
                        float: left;
                        margin: 10px;
                    }
                </style>
                <div class="button" onclick="Catalogue.doCache('all')">预留按钮</div>
            `,
        })
    }
};

export default Catalogue;
