/***************************************************************************
 * 上传图片
 *
 * @returns {Boolean}
 */
function ajaxFileUpload(num, ds) {
    var filePath = $("#picture" + num).val();
    var extStart = filePath.lastIndexOf("\.");
    var supportPicExt = ".BMP|.PNG|.GIF|.JPG|.JPEG";
    var ext = filePath.substring(extStart, filePath.length).toUpperCase();
    if (supportPicExt.lastIndexOf(ext) == -1) {
        alert("图片限于bmp,png,gif,jpeg,jpg格式");
        $("#uppic" + num).val('');
        return;
    }
    var url = ds + '/file/upload?sourcePic=picture' + num;
    $.ajaxFileUpload({
        url: url,
        secureuri: false,
        fileElementId: 'picture' + num,
        dataType: 'text',
        success: function (result) {
            var resultJson = jQuery.parseJSON(result);
            if("success"== resultJson.status){
                processAfterUpload(resultJson.filePath,resultJson.fileName, ds, num);
            }else{
                $.error(resultJson.message);
            }

        },
        error: function (data, status, e) {
            alert(e);
        }
    });
    return false;
}

function processAfterUpload(filePath,fileName, ds, num) {
    $("#filePath" + num).val(fileName);
    $("#image" + num).attr("src",filePath);
}


