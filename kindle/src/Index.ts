import Bar from './common/bar/bar';
import {getSpecialParent, PageInfo, UserArticle} from './common/common';
import Pagination from './common/pagination/pagination';

class Index {
    element: HTMLElement;
    bar: Bar;
    pagination: Pagination;

    pageInfo: PageInfo;
    articleList: UserArticle[] = [];

    loading: boolean = false;

    constructor() {
        this.element = document.querySelector('.page.index');

        this.pagination = new Pagination({
            root: this.element.querySelector('.content'),
            fake: true,
            pageChange: (index: number) => {
                this.getArticleList(index)
            }
        });

        this.bar = new Bar({
            element: this.element.querySelector('.bar'),
            pagination: this.pagination
        });

        window.Bind.bindView(this.element.querySelector('.article-list'), this, 'articleList', (articleList: UserArticle[]) => {
            if (this.pageInfo) {
                this.pagination.pageIndex = this.pageInfo.number
                this.pagination.pageLimit = this.pageInfo.totalPages
            }
            let html = ''
            articleList.forEach(book => {
                let haveread = book.status == 1 ? 'haveread' : '';
                let star = book.status == 2 ? 'star' : '';
                html += `
                    <div class="article-item ${haveread} ${star}" feedId="${book.feedId}" articleId="${book.articleId}" >
                        <div class="article-feed">${book.feedName}
                            <span>${book.publishedDate}</span>
                        </div>
                        <div class="article-title">${book.title}</div>
                    </div>
                `;
            })
            return html;
        });

        window.Router.cbMap.index = function () {
            window.Index.getArticleList(window.Index.pageInfo ? window.Index.pageInfo.number : 0, true);
        }
    }

    getArticleList(toPage: number = 0, force: boolean = false): void {
        if (this.loading === true) {
            window.Message.add({content: '正在加载阅读清单'});
            return;
        }
        this.loading = true;
        if (this.pageInfo && this.pageInfo.number == toPage && !force) {
            this.loading = false;
            return;
        }
        window.Api.getUserArticleList(toPage, {
            success: (res: any) => {
                this.loading = false;
                this.pageInfo = res.data
                this.articleList = res.data.content
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
        let articleId = item.getAttribute('articleId');
        let feedId = item.getAttribute('feedId');
        window.Store.set('current_feed', feedId);
        window.Store.set('current_article', articleId);
        window.setTimeout(() => {
            window.Router.go('article');
        });
    }
};

export default Index;
