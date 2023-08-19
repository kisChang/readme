class Pagination {
    root: HTMLElement;
    box: HTMLElement;
    padding: HTMLElement;

    pageStep: number;

    pageIndex: number = 0;

    pageLimit: number = 1;

    pagePadding: number = 0;

    fakePage: boolean = false;

    constructor(config: {
        root: HTMLElement,
        fake?: boolean
        pageChange?: Function
        pageStepCalc?: Function
    }) {
        this.root = config.root;
        this.handleHtml(config.root);

        this.fakePage = config.fake || false;

        this.pageStep = this.box.offsetHeight;

        this.checkPage();

        window.Bind.bindStyle(this.padding, this, 'pagePadding', 'height', (v: any) => `${v}px`);
        window.Bind.bind(this, 'pageIndex', (value: number) => {
            if (this.fakePage) {
                config?.pageChange(value);
                return;
            }
            if (typeof config.pageStepCalc === "function") {
                this.box.scrollTop = config.pageStepCalc(value);
            }else {
                this.box.scrollTop = this.pageStep * value;
            }
        });
    }

    private handleHtml(root: HTMLElement) {
        let inner = root.innerHTML;
        root.innerHTML = `
            <div class="pagination-box">
                <div class="pagination-body">
                    <div class="pagination-content"></div>
                    <div class="pagination-padding"></div>
                </div>
            </div>`;
        let content: HTMLElement = root.querySelector('.pagination-content');
        content.innerHTML = inner;
        this.box = root.querySelector('.pagination-box');
        this.padding = root.querySelector('.pagination-padding');
    }

    checkPage(limit?: number): void {
        this.pageStep = this.box.offsetHeight;
        if (this.fakePage) {
            this.pageLimit = limit || 1;
            this.pagePadding = 0;
            return;
        }
        this.pageLimit = Math.ceil(this.box.scrollHeight / this.pageStep) || 1;
        // TODO  暂时方案，少滚动一部分内容 (- 的内容)
        this.pagePadding = this.pageStep * this.pageLimit - this.pageStep * this.box.scrollHeight;
    }

    setPage(num: number) {
        let target = num;
        if (num < 0) {
            target = 0;
        }
        if (num >= this.pageLimit) {
            target = this.pageLimit - 1;
        }
        this.pageIndex = target;
    }

    pageChange(add: number) {
        this.setPage(this.pageIndex + add);
    }
};

export default Pagination;
