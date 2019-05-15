/**
 * 测试自定义协议
 */

/**
 * 根据字符串消息mess创建一个二进制消息(用于文件上传)
 * @param mess(类型:String)
 * @return  ArrayBuffer
 */
function createMessByOwnPro(fileName, fileUrl) {

    var mess = fileName + "<<*>>" + fileUrl;
    return writeStringToArrayBuffer(mess);
}

/**
 * 将字符串数据写入到ArrayBuffer中去
 * @param fileName 需要写入的消息(字符串对象)
 *
 * @return ArrayBuffer 二进制消息 ArrayBuffer对象
 */
function writeStringToArrayBuffer(mess) {

    var buf = new ArrayBuffer(mess.length * 2);
    var bufView = new Uint16Array(buf);
    var strLen = mess.length;
    for (var i = 0; i < strLen; i++) {
        bufView[i] = mess.charCodeAt(i);
    }

    return buf;
}


