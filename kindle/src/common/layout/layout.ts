interface LayoutInterface {
    fontSize: number;
    lineHeight: number;
};

class Layout {

    htmlFontSize: number; // 记录基础字体大小
    fontSize: number;
    lineHeight: number;

    limit: LayoutInterface = {
        fontSize: 1,
        lineHeight: 1
    };
    base: LayoutInterface = {
        fontSize: 2,
        lineHeight: 2
    };

    constructor() {
        if (window.Layout) {
            throw Error('layout has been inited');
        }
        window.Layout = this;

        document.addEventListener('DOMContentLoaded', () => {
            // 初始化的fontSize大小
            let fontSize = window.innerWidth / 10
            fontSize = fontSize > 50 ? 50 : fontSize
            this.htmlFontSize = fontSize
            // 写入标准css
            const html = document.querySelector('html')
            html.style.fontSize = fontSize + 'px'
        })
        this.fontSize = parseFloat(window.Store.get('fontSize') || this.base.fontSize.toString());
        this.lineHeight = parseFloat(window.Store.get('lineHeight') || this.base.lineHeight.toString());
    }

    set(target: 'fontSize' | 'lineHeight', value?: number): void {
        this[target] = value || this.base[target];
        window.Store.set(target, this[target].toString());
    }

    add(target: 'fontSize' | 'lineHeight', num: number): void {
        let current = this[target];
        current += num;

        if (current < this.limit[target]) {
            current = this.limit[target];
        }
        this.set(target, parseFloat(current.toFixed(1)));

        if (target === 'fontSize') { // 同步更新lineHeight
            this.add('lineHeight', num)
        }
    }

    reset(target?: 'fontSize' | 'lineHeight'): void {
        if (target) {
            this.set(target);
            return;
        }
        this.set('fontSize');
        this.set('lineHeight');
    }
};

export default Layout;
