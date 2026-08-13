import Clipboard from 'clipboard';
import * as msg from '@/hooks/web/msg';

function clipboardSuccess() {
  msg.createSuccessTip('复制成功');
}

function clipboardError() {
  msg.createError('复制失败');
}

export default function handleClipboard(text: string) {
  try {
    Clipboard.copy(text);
    clipboardSuccess();
  } catch {
    clipboardError();
  }
}
