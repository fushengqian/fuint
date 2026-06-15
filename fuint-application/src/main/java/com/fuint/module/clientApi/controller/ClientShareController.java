package com.fuint.module.clientApi.controller;

import com.aliyun.oss.OSS;
import com.fuint.common.dto.commission.CommissionRelationDto;
import com.fuint.common.dto.member.UserInfo;
import com.fuint.common.enums.PlatformTypeEnum;
import com.fuint.common.enums.StatusEnum;
import com.fuint.common.param.CommissionRelationPage;
import com.fuint.common.service.*;
import com.fuint.common.util.AliyunOssUtil;
import com.fuint.common.util.QRCodeUtil;
import com.fuint.common.util.TokenUtil;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.framework.web.BaseController;
import com.fuint.framework.web.ResponseObject;
import com.fuint.repository.model.MtUser;
import com.fuint.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 邀请controller
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Api(tags="会员端-邀请相关接口")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/clientApi/share")
public class ClientShareController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ClientShareController.class);

    private Environment env;

    /**
     * 分佣提成关系服务接口
     * */
    private CommissionRelationService commissionRelationService;

    /**
     * 微信相关服务接口
     * */
    private WeixinService weixinService;

    /**
     * 商户服务接口
     */
    private MerchantService merchantService;

    /**
     * 会员服务接口
     * */
    private MemberService memberService;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 获取邀请列表
     */
    @ApiOperation(value="获取邀请列表", notes="获取邀请列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject list(HttpServletRequest request,  @RequestBody CommissionRelationPage commissionRelationPage) throws BusinessCheckException {
        String merchantNo = request.getHeader("merchantNo") == null ? "" : request.getHeader("merchantNo");
        UserInfo userInfo = TokenUtil.getUserInfo();

        commissionRelationPage.setStatus(StatusEnum.ENABLED.getKey());
        commissionRelationPage.setUserId(userInfo.getId());
        if (StringUtil.isNotEmpty(merchantNo)) {
            commissionRelationPage.setMerchantNo(merchantNo);
        }

        PaginationResponse<CommissionRelationDto> paginationResponse = commissionRelationService.queryRelationByPagination(commissionRelationPage);
        Map<String, Object> outParams = new HashMap();
        String url = env.getProperty("website.url");
        outParams.put("url", url);
        outParams.put("paginationResponse", paginationResponse);

        return getSuccessResult(outParams);
    }

    /**
     * 生成小程序链接
     */
    @ApiOperation(value = "生成小程序链接")
    @RequestMapping(value = "/getMiniAppLink", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject getMiniAppLink(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        UserInfo mtUser = TokenUtil.getUserInfo();
        String path = param.get("path") == null ? "" : param.get("path").toString();
        String query = param.get("query") == null ? "" : param.get("query").toString();
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));

        if (merchantId == null || merchantId <= 0) {
            MtUser userInfo = memberService.queryMemberById(mtUser.getId());
            if (userInfo != null) {
                merchantId = userInfo.getMerchantId();
            }
        }

        String link = weixinService.createMiniAppLink(merchantId, path, query);

        Map<String, Object> outParams = new HashMap();
        outParams.put("link", link);

        ResponseObject responseObject = getSuccessResult(outParams);
        return getSuccessResult(responseObject.getData());
    }

    /**
     * 获取分享海报二维码
     */
    @ApiOperation(value = "获取分享海报二维码")
    @RequestMapping(value = "/getShareQrCode", method = RequestMethod.POST)
    @CrossOrigin
    public ResponseObject getShareQrCode(HttpServletRequest request, @RequestBody Map<String, Object> param) throws BusinessCheckException {
        UserInfo mtUser = TokenUtil.getUserInfo();
        String page = param.get("path") == null ? "pages/index/index" : param.get("path").toString();
        String query = param.get("query") == null ? "" : param.get("query").toString();
        Integer width = param.get("width") == null ? 430 : Integer.parseInt(param.get("width").toString());
        String platform = param.get("platform") == null ? "mp" : param.get("platform").toString();
        Integer merchantId = merchantService.getMerchantId(request.getHeader("merchantNo"));

        if (merchantId == null || merchantId <= 0) {
            MtUser userInfo = memberService.queryMemberById(mtUser.getId());
            if (userInfo != null) {
                merchantId = userInfo.getMerchantId();
            }
        }

        String qrCodeUrl;

        if (PlatformTypeEnum.H5.getCode().equalsIgnoreCase(platform)) {
            // H5 端：生成本地二维码，指向网页链接
            String fullUrl = page;
            if (StringUtil.isNotEmpty(query)) {
                fullUrl = page + "#?" + query;
            }
            qrCodeUrl = generateH5QrCode(mtUser.getId(), fullUrl, width);
        } else {
            // 小程序端：调用微信 API 生成小程序码，然后读取本地文件返回 base64
            String fullPath = page;
            if (StringUtil.isNotEmpty(query)) {
                fullPath = page + "?" + query;
            }
            // 此方法会保存文件到本地，但返回的可能是 OSS URL
            weixinService.createQrCode(merchantId, "share", mtUser.getId(), fullPath, width);

            // 直接读取本地文件返回 base64，避免小程序 downloadFile 域名白名单问题
            String pathRoot = env.getProperty("images.root");
            String baseImage = env.getProperty("images.path");
            String filePath = "Qrshare" + mtUser.getId() + ".png";
            String localPath = pathRoot + baseImage + filePath;
            try {
                byte[] fileBytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(localPath));
                qrCodeUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(fileBytes);
            } catch (Exception e) {
                logger.error("读取小程序码本地文件失败：{}", e.getMessage());
                throw new BusinessCheckException("生成小程序码出错");
            }
        }

        // 如果是相对路径且非 base64 数据，拼接完整域名
        if (StringUtil.isNotEmpty(qrCodeUrl) && !qrCodeUrl.startsWith("http") && !qrCodeUrl.startsWith("data:")) {
            String domain = settingService.getUploadBasePath();
            if (StringUtil.isNotEmpty(domain)) {
                if (domain.endsWith("/")) {
                    domain = domain.substring(0, domain.length() - 1);
                }
                if (!qrCodeUrl.startsWith("/")) {
                    qrCodeUrl = "/" + qrCodeUrl;
                }
                qrCodeUrl = domain + qrCodeUrl;
            }
        }

        Map<String, Object> outParams = new HashMap();
        outParams.put("qrCode", qrCodeUrl);

        ResponseObject responseObject = getSuccessResult(outParams);
        return getSuccessResult(responseObject.getData());
    }

    /**
     * 生成 H5 端二维码，直接返回 base64 编码图片（避免前端跨域下载问题）
     */
    private String generateH5QrCode(Integer userId, String content, Integer width) throws BusinessCheckException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            QRCodeUtil.createQrCode(outputStream, content, width, width, "png", "");

            byte[] bytes = outputStream.toByteArray();

            // 保存到本地
            String pathRoot = env.getProperty("images.root");
            String baseImage = env.getProperty("images.path");
            String filePath = "Qrh5share" + userId + ".png";
            String path = pathRoot + baseImage + filePath;
            com.fuint.utils.QRCodeUtil.saveQrCodeToLocal(bytes, path);

            // 上传阿里云 OSS（仅用于备份）
            String mode = env.getProperty("aliyun.oss.mode");
            if ("1".equals(mode)) {
                String endpoint = env.getProperty("aliyun.oss.endpoint");
                String accessKeyId = env.getProperty("aliyun.oss.accessKeyId");
                String accessKeySecret = env.getProperty("aliyun.oss.accessKeySecret");
                String bucketName = env.getProperty("aliyun.oss.bucketName");
                String folder = env.getProperty("aliyun.oss.folder");
                OSS ossClient = AliyunOssUtil.getOSSClient(accessKeyId, accessKeySecret, endpoint);
                File ossFile = new File(path);
                AliyunOssUtil.upload(ossClient, ossFile, bucketName, folder);
            }

            // 直接返回 base64 编码，前端无需二次下载，避免跨域问题
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            logger.error("生成 H5 二维码出错：{}", e.getMessage());
            throw new BusinessCheckException("生成 H5 二维码出错");
        }
    }
}
