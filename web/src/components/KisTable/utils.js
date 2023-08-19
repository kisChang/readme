
/**
 * 遍历children形式数据
 * @param data 需要遍历的数据
 * @param callback 回调
 * @param childrenField children字段名
 */
export function eachTreeData(data, callback, childrenField = 'children') {
  data.forEach((d) => {
    if (callback(d) !== false && d[childrenField]?.length) {
      eachTreeData(d[childrenField], callback, childrenField);
    }
  });
}

/**
 * 处理children形式数据
 * @param data 需要处理的数据
 * @param formatter 处理器
 * @param childrenField children字段名
 * @returns {[]} 处理后的数据
 */
export function formatTreeData(data, formatter, childrenField = 'children') {
  const result = [];
  data.forEach((d) => {
    const item = formatter(d);
    if (item !== false) {
      if (item[childrenField]?.length) {
        item[childrenField] = formatTreeData(
          item[childrenField],
          formatter,
          childrenField
        );
      }
      result.push(item);
    }
  });
  return result;
}


/**
 * 生成随机字符串
 * @param length 长度
 * @param radix 基数
 * @returns {string}
 */
export function uuid(length = 32, radix) {
  const num = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += num.charAt(Math.floor(Math.random() * (radix || num.length)));
  }
  return result;
}
