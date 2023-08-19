import {FeedSource, makeDisplayText} from '../common/common';

class Config {
    element: HTMLElement;

    displayText: string;

    constructor() {
        this.element = document.querySelector('.page.config');
        this.displayText = makeDisplayText(200);

        window.Bind.bindView(this.element.querySelector('.font-size'), window.Layout, 'fontSize');
        window.Bind.bindView(this.element.querySelector('.line-height'), window.Layout, 'lineHeight');

        let display: HTMLElement = this.element.querySelector('.display .text p');
        window.Bind.bindView(display, this, 'displayText');
        window.Bind.bindStyle(display, window.Layout, 'fontSize', 'fontSize', (v: any) => `${v}rem`);
        window.Bind.bindStyle(display, window.Layout, 'lineHeight', 'lineHeight', (v: any) => `${v}rem`);
    }

    // 扫码登录设备功能
    loginQrCode: string | null;
    loginQrCodeId: -1;

    getLoginQr(force: boolean = false): void {
        if (!force && window.Config.loginQrCodeId > 0) {
            return
        }
        let param = {
            appName: navigator.appName,
            appCodeName: navigator.appCodeName,
            appVersion: navigator.appVersion,
            platform: navigator.platform,
            userAgent: navigator.userAgent,
        }
        window.Api.post("/open-api/user/get_login_qr", param, {
            success: (res: any) => {
                document.getElementById('loginQrImg').setAttribute('src', res.data.qrCode)
                window.Config.loginQrCode = res.data.code
                if (window.Config.loginQrCodeId > 0) {
                    clearInterval(window.Config.loginQrCodeId)
                }
                // @ts-ignore
                window.Config.loginQrCodeId = setInterval(window.Config.loopLoginQrState, 1000)
            },
        })
    }
    loopLoginQrState(): void {
        window.Api.get('/open-api/user/loop_login_status', {code: window.Config.loginQrCode}, {
            success: (res: any) => {
                if (res.data.state == 1) {
                    // 登录成功
                    window.Message.add({content: "登录成功"});
                    clearInterval(window.Config.loginQrCodeId)
                    window.Store.setToken(res.data.code)
                    // 跳转首页
                    window.Router.go('index')
                    window.BookShelf.getBookShelf()
                }
            }
        })
    }
};

export default Config;
