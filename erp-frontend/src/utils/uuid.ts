const hexList: string[] = [];
for (let i = 0; i <= 15; i++) {
  hexList[i] = i.toString(16);
}

// 用于确保generateCode生成的编号唯一性的计数器
let codeCounter = 0;

export function generateCode(): string {
  // 生成编号 必须由字母、数字、"-_."
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  const specialChars = '-_.';
  
  // 使用当前时间作为基础确保唯一性
  const now = new Date();
  const timestamp = now.getTime();
  
  // 增加计数器确保即使在同一毫秒内生成的编号也不会重复
  codeCounter++;
  
  // 将时间转换为36进制字符串，更短且包含字母和数字
  const timeStr = timestamp.toString(36);
  
  // 生成3-4位的随机前缀（字母开头）
  let prefix = chars[Math.floor(Math.random() * 52)]; // 确保第一个字符是字母
  const prefixLength = Math.floor(Math.random() * 2) + 2; // 2-3位额外随机字符
  
  for (let i = 0; i < prefixLength; i++) {
    prefix += chars[Math.floor(Math.random() * chars.length)];
  }
  
  // 添加一个特殊字符作为分隔符
  const separator = specialChars[Math.floor(Math.random() * specialChars.length)];
  
  // 组合编号：前缀 + 分隔符 + 时间戳 + 计数器
  let result = prefix + separator + timeStr + (codeCounter % 1000).toString(36);
  
  // 如果编号太长，截取合适长度（保留前缀、分隔符和足够的唯一性信息）
  if (result.length > 16) {
    // 保留前缀、分隔符和时间戳的后部分
    result = prefix + separator + timeStr.slice(-8) + (codeCounter % 1000).toString(36);
  }
  
  // 确保结尾不是特殊字符
  if (specialChars.includes(result[result.length - 1])) {
    result = result.slice(0, -1) + chars[Math.floor(Math.random() * chars.length)];
  }
  
  return result;
}

export function buildUUID(): string {
  let uuid = '';
  for (let i = 1; i <= 36; i++) {
    if (i === 9 || i === 14 || i === 19 || i === 24) {
      uuid += '-';
    } else if (i === 15) {
      uuid += 4;
    } else if (i === 20) {
      uuid += hexList[(Math.random() * 4) | 8];
    } else {
      uuid += hexList[(Math.random() * 16) | 0];
    }
  }
  return uuid.replace(/-/g, '');
}

let unique = 0;
export function buildShortUUID(prefix = ''): string {
  const time = Date.now();
  const random = Math.floor(Math.random() * 1000000000);
  unique++;
  return prefix + '_' + random + unique + String(time);
}



