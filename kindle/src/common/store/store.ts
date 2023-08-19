import { compress, decompress } from 'lz-string';

const TOKEN_NAME = "Token"

class Store {
    data: any;
    en_compress: boolean = false;

    constructor() {
        if (window.Store) {
            return;
        }
        window.Store = this;
    }

    getObj(key: string): any | null {
        return JSON.parse(this.get(key));
    }

    setObj(key: string, value: any, cb?: {success?: Function, fail?: Function}): void {
        this.set(key, JSON.stringify(value), cb);
    }

    set(key: string, value: string, cb?: {success?: Function, fail?: Function}): void {
        try {
            let cKey = this.en_compress ? compress(key) : key;
            let cValue = this.en_compress ? compress(value) : value;
            localStorage.setItem(cKey, cValue);
            cb && cb.success && cb.success();
        } catch(e) {
            window.Message.add({content: '缓存失败，空间不足'});
            cb && cb.fail && cb.fail();
        }
    }

    get(key: string): string | null {
        let cKey = this.en_compress ? decompress(key) : key;
        let store = localStorage.getItem(cKey);
        if (store) {
            return this.en_compress ? decompress(store) : store;
        }
        return null;
    }

    del(key: string): void {
        localStorage.removeItem(key);
        this.setCookie(key, null, 0)
    }

    has(key: string): boolean {
        if (localStorage.hasOwnProperty(key)) {
            return true;
        }
        return this.getCookie(key) != null;
    }

    getCookie(cname: string): string | null {
        let name = cname + "=";
        let ca = document.cookie.split(';');
        for (let i = 0; i < ca.length; i++) {
            let c = ca[i].trim();
            if (c.indexOf(name) == 0) return c.substring(name.length, c.length);
        }
        return null;
    }

    setCookie(name: string, value: string, exDays: number = 1) {
        let d = new Date();
        d.setTime(d.getTime() + (exDays * 24 * 60 * 60 * 1000));
        // @ts-ignore
        let expires = "expires=" + d.toGMTString();
        document.cookie = name + "=" + value + "; " + expires;
    }

    getToken(): string | null {
        return this.get(TOKEN_NAME) || this.getCookie(TOKEN_NAME)
    }

    setToken(token: string): void {
        this.set(TOKEN_NAME, token)
        // token 有效期为30天
        this.setCookie(TOKEN_NAME, token, 30)
    }

    getByHead(head: string): string[] {
        return Object.keys(localStorage).filter(v => v.indexOf(head) === 0);
    }

    logout(): void {
        window.Modal.add({
            content: `<h2 style="text-align: center;">确认登出系统？</h2>`,
            onOk: function () {
                window.Store.setCookie(TOKEN_NAME, null, 0)
                localStorage.clear()
                sessionStorage.clear()
                location.reload()
            }
        })
    }
};

export default Store;
