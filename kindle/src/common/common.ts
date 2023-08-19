function strToDom(str: string): HTMLCollection {
    let div = document.createElement('div');
    div.innerHTML = str;
    return div.children;
}

function makeDisplayText(time: number): string {
    let text = '测试文本';
    return new Array(time + 1).join(text);
}

function getSpecialParent(ele: HTMLElement,checkFun: Function): HTMLElement | null {
    if (ele && ele !== document as unknown && checkFun(ele)) {
        return ele;
    }
    let parent = ele.parentElement || ele.parentNode;
    return parent?getSpecialParent(parent as HTMLElement, checkFun):null;
}

function getObject(source: any, keys: string[], others?: {[key: string]: any}): any {
    let obj: any = {};
    keys.forEach(key => {
        obj[key] = source[key];
    });
    others && Object.keys(others).forEach(key => {
        obj[key] = others[key];
    });
    return obj;
}

function changeValueWithNewObj(obj: any, target: {[key: string]: any}): any {
    let result = JSON.parse(JSON.stringify(obj));
    Object.keys(target).forEach(v => {
        result[v] = target[v];
    });
    return result;
}

interface FeedSource {
    feedId: string;
    source: string;
    name: string;
    logo: string;
    url: string;
    descText: string;
    lastUpdate: string;
}

interface CatalogueItem {
    index: number;
    articleId: number;
    publishedDate: string;
    status: number;
    title: string;
}

interface PageInfo {
    number: number;
    totalElements: number;
    totalPages: number;
}

interface Progress {
    index: number;
    pos: number;
    time: number;
    title: string;
}

interface UserArticle {
    articleId: number;
    feedId: number;
    status: number;

    source: string;
    title: string;
    publishedDate: string;
    feedName: string;
}

export { strToDom, makeDisplayText, getSpecialParent, getObject, changeValueWithNewObj, UserArticle, PageInfo, FeedSource, CatalogueItem, Progress };
