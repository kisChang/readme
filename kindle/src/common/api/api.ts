class Api {
    base_url: string = '';

    constructor() {
        if (window.Api) {
            throw Error('api has been inited');
        }
        window.Api = this;
    }

    // 获取单篇文章内容
    getArticle(articleId: string, index: number, cb?: { success?: Function, error?: Function }): void {
        this.get('/user-api/user_article/get_one', {articleId: articleId, index: index}, {
            success: (data: any) => {
                cb && cb.success && cb.success(data);
            },
            error: (err: any) => {
                console.log(err);
                cb && cb.error && cb.error(err);
                window.Message.add({content: '获取文章内容失败'});
            }
        });
    }

    // 获取单个订阅的文章列表
    getCatalogue(feedId: string, page: number, cb?: { success?: Function, error?: Function }): void {
        this.get('/user-api/user_article/list', {feedId: feedId, size: 6, page: page}, {
            success: (data: any) => {
                cb && cb.success && cb.success(data);
            },
            error: (err: any) => {
                console.log(err);
                cb && cb.error && cb.error(err);
                window.Message.add({content: '获取目录内容失败'});
            }
        });
    }

    // 获取整体的文章列表
    getUserArticleList(page: number, cb?: { success?: Function, error?: Function }): void {
        this.get('/user-api/user_article/list_all', {size: 5, page: page}, {
            success: (data: any) => {
                cb && cb.success && cb.success(data);
            },
            error: (err: any) => {
                console.log(err);
                cb && cb.error && cb.error(err);
                window.Message.add({content: '获取文章列表失败'});
            }
        });
    }

    // 获取订阅清单
    getBookshelf(cb?: { success?: Function, error?: Function }): void {
        this.get('/user-api/feed_source/self', {}, {
            success: (data: any) => {
                cb && cb.success && cb.success(data);
            },
            error: (err: any) => {
                console.log(err);
                cb && cb.error && cb.error(err);
                window.Message.add({content: '获取书架内容失败'});
            }
        });
    }

    post(url: string, data: { [key: string]: any }, cb?: { success?: Function, error?: Function, check?: boolean }) {
        return this.http('POST', url, data, cb);
    }

    get(url: string, data: { [key: string]: any }, cb?: { success?: Function, error?: Function, check?: boolean }) {
        return this.http('GET', url, data, cb);
    }

    http(method: 'GET' | 'POST', url_param: string, data: { [key: string]: any }, cb?: {
        success?: Function,
        error?: Function,
        check?: boolean
    }) {
        // 创建 XMLHttpRequest，相当于打开浏览器
        const xhr = new XMLHttpRequest();

        let url = this.base_url + '/api' + url_param

        // 打开一个与网址之间的连接   相当于输入网址
        // 利用open（）方法，第一个参数是对数据的操作，第二个是接口
        // xhr.open(method, `${url}?${Object.keys(data).map(v => `${v}=${data[v]}`).join('&')}`);
        let param: string = Object.keys(data).map(v => `${v}=${data[v]}`).join('&');
        xhr.open(method, method === 'GET' ? `${url}?${param}` : url);

        if (method === 'POST') {
            xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
        }
        if (window.Store.getToken()) {
            xhr.setRequestHeader('Authorization', window.Store.getToken());
        }

        // 通过连接发送请求  相当于点击回车或者链接
        xhr.send(method === 'GET' ? null : JSON.stringify(data));

        // 指定 xhr 状态变化事件处理函数   相当于处理网页呈现后的操作
        // 全小写
        xhr.onreadystatechange = function () {
            // 通过readyState的值来判断获取数据的情况
            if (this.readyState === 4) {
                // 响应体的文本 responseText
                let response;
                try {
                    response = JSON.parse(this.responseText);
                } catch (e) {
                    response = this.responseText;
                }
                if (this.status === 200 && response.state) {
                    cb && cb.success && cb.success(response);
                } else {
                    if (cb && cb.error) {
                        cb.error(response);
                    } else {
                        window.Message.add({content: response.msg});
                    }
                }
            }
        }

        return xhr;
    }

};

export default Api;
