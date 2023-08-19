import Bar from '../common/bar/bar';
import { FeedSource, getObject, getSpecialParent, Progress } from '../common/common';
import Pagination from '../common/pagination/pagination';

class BookShelf {
    element: HTMLElement;
    bar: Bar;
    pagination: Pagination;

    bookMap: {[key: string]: FeedSource} = {};
    bookList: FeedSource[] = [];


    loading: boolean = false;

    constructor() {
        this.element = document.querySelector('.page.bookshelf');

        this.pagination = new Pagination({
            root: this.element.querySelector('.content')
        });

        this.bar = new Bar({
            element: this.element.querySelector('.bar'),
            pagination: this.pagination
        });

        this.bookList = [];
        this.compareBookList(this.bookList);

        window.Bind.bindView(this.element.querySelector('.book-list'), this, 'bookList', (bookList: FeedSource[], oldV: FeedSource[] = []) => {
            this.compareBookList(bookList);
            let height = (this.element.querySelector('.pagination-box') as HTMLElement).offsetHeight / 4;
            let imgWidth = height * 3 / 4;
            let width = Math.floor((this.element.querySelector('.book-list') as HTMLElement).offsetWidth / 2);
            let html = `
                <style>
                    .book-item {height: ${height}px;}
                    .book-item .book-cover {width: ${imgWidth}px;}
                    .book-item .book-info {width: ${width - imgWidth - 30}px;}
                </style>
            `;
            bookList.forEach(book => {
                html += `
                    <div class="book-item" key="${book.feedId}">
                        <img class="book-cover" src="${book.logo}" alt="${book.name}"/>
                        <div class="book-info">
                            <div class="book-name">${book.name}</div>
                            <div class="book-latest">${book.descText}</div>
                            <div class="book-latest-time">at ${book.lastUpdate}</div>
                        </div>
                    </div>
                `;
            })
            window.setTimeout(() => {
                this.pagination.checkPage();
            });
            return html;
        });

        window.Router.cbMap.bookshelf = this.getBookShelf
    }

    compareBookList(newV: FeedSource[]): void {
        let oldMap = this.bookMap;
        this.bookMap = {};
        newV.forEach(book => {
            this.bookMap[book.feedId] = book;
            if (oldMap[book.feedId]) {
                delete oldMap[book.feedId];
            }
        });
    }

    getBookShelf(): void {
        if (this.loading === true) {
            window.Message.add({content: '正在加载书架数据'});
            return;
        }
        this.loading = true;
        window.Api.getBookshelf({
            success: (res: any) => {
                this.loading = false;
                let bookList: FeedSource[] = res.data.content.map((book: any) => {
                    let id = book.feedId;
                    let keys: string[] = ['name', 'logo', 'url', 'descText', 'lastUpdate'];
                    return getObject(book, keys, {
                        id: id,
                        feedId: book.feedId,
                        source: book.bookUrl
                    });
                });
                this.bookList = [].concat(bookList);
            },
            error: (err: any) => {
                this.loading = false;
            }
        });
    }

    clickItem(event: Event): void {
        let item = getSpecialParent((event.target || event.srcElement) as HTMLElement, (ele: HTMLElement) => {
            return ele.classList.contains('book-item');
        });
        let feedId = item.getAttribute('key');
        window.Store.set('current_feed', feedId);
        window.Router.go('catalogue');
    }
};

export default BookShelf;
