package com.lframework.xingyun.shkb.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.lframework.starter.common.exceptions.impl.DefaultClientException;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.starter.web.core.impl.BaseMpServiceImpl;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.starter.web.core.utils.PageHelperUtil;
import com.lframework.starter.web.core.utils.PageResultUtil;
import com.lframework.xingyun.basedata.entity.MachineType;
import com.lframework.xingyun.basedata.entity.Product;
import com.lframework.xingyun.basedata.mappers.MachineTypeMapper;
import com.lframework.xingyun.basedata.mappers.ProductMapper;
import com.lframework.xingyun.shkb.entity.MachineInfo;
import com.lframework.xingyun.shkb.entity.MachineTaskMagneticPowder;
import com.lframework.xingyun.shkb.mappers.MachineTaskMagneticPowderMapper;
import com.lframework.xingyun.shkb.service.MachineInfoService;
import com.lframework.xingyun.shkb.service.MachineTaskMagneticPowderService;
import com.lframework.xingyun.shkb.dto.RemoteFolderDto;
import com.lframework.xingyun.shkb.dto.RemoteFileDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
* 磁粉机任务服务实现
*/
@Service
@Slf4j
public class MachineTaskMagneticPowderServiceImpl extends BaseMpServiceImpl<MachineTaskMagneticPowderMapper, MachineTaskMagneticPowder>
    implements MachineTaskMagneticPowderService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private MachineInfoService machineInfoService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private MachineTypeMapper machineTypeMapper;

    @Override
    public PageResult<MachineTaskMagneticPowder> query(Integer pageIndex, Integer pageSize,
                                                       String taskId, String contractNo, String partNo, String serialNo,
                                                       Integer machineTaskStatus) {
        PageHelperUtil.startPage(pageIndex, pageSize);
        List<MachineTaskMagneticPowder> datas = this.getBaseMapper().selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery(MachineTaskMagneticPowder.class)
                        .like(StringUtil.isNotBlank(taskId), MachineTaskMagneticPowder::getTaskId, taskId)
                        .like(StringUtil.isNotBlank(contractNo), MachineTaskMagneticPowder::getContractNo, contractNo)
                        .like(StringUtil.isNotBlank(partNo), MachineTaskMagneticPowder::getPartNo, partNo)
                        .like(StringUtil.isNotBlank(serialNo), MachineTaskMagneticPowder::getSerialNo, serialNo)
                        .eq(machineTaskStatus != null, MachineTaskMagneticPowder::getMachineTaskStatus, machineTaskStatus)
                        .orderByDesc(MachineTaskMagneticPowder::getCreateTime)
        );
        return PageResultUtil.convert(new PageInfo<>(datas));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(String taskId) {
        if (StringUtil.isBlank(taskId)) {
            throw new DefaultClientException("任务ID不能为空！");
        }
        // 锁住任务行，避免同一任务被浏览器重复点击或多个实例并发下发。
        MachineTaskMagneticPowder task = this.getBaseMapper().selectByTaskIdForUpdate(taskId);
        if (task == null) {
            throw new DefaultClientException("未找到磁粉机任务！");
        }
        MachineTaskRules.requireMagneticSendable(task.getMachineTaskStatus());

        // 获取磁粉机设备IP（machineType=2）
        MachineInfo machine = machineInfoService.lambdaQuery()
                .eq(MachineInfo::getMachineType, 2)
                .last("limit 1")
                .one();
        if (machine == null || StringUtil.isBlank(machine.getIpAddress())) {
            throw new DefaultClientException("未配置磁粉机设备IP地址！");
        }

        // 机型、序列号、任务号
        LambdaQueryWrapper<Product> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Product::getCode, task.getPartNo());
        Product product = productMapper.selectOne(lqw);
        if (product == null) {
            throw new DefaultClientException("航次件号不存在！");
        }
        String machineTypeId = product.getMachineTypeId();
        if (machineTypeId == null) {
            throw new DefaultClientException("件号未配置机型！");
        }
        if (StringUtil.isBlank(machineTypeId)) {
            throw new DefaultClientException("件号未配置机型！");
        }
        MachineType machineInfo = machineTypeMapper.selectById(machineTypeId);
        if(machineInfo == null) {
            throw new DefaultClientException("未找到机型！");
        }
        String modelNum = machineInfo.getName();
        String snNum = task.getSerialNo();
        String taskNum = task.getTaskId();

        // 发送表单请求（5秒超时）
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        String url = "http://" + machine.getIpAddress() + "/api/capture";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("modelNum", modelNum);
        form.add("snNum", snNum);
        form.add("taskNum", taskNum);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        log.info("磁粉机任务下发请求，url={}，form={}", url, form);
        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(url, request, String.class);
        } catch (ResourceAccessException e) {
            // 包含连接/读取超时
            log.warn("磁粉机任务下发网络异常：{}", e.getMessage());
            throw new DefaultClientException("磁粉机网络连接错误");
        } catch (Exception e) {
            log.warn("磁粉机任务下发异常：{}", e.getMessage(), e);
            throw new DefaultClientException("下发失败");
        }
        log.info("磁粉机任务下发响应，status={}，body={}", response.getStatusCodeValue(), response.getBody());
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new DefaultClientException("下发失败：" + response.getStatusCode());
        }
        try {
            String body = response.getBody();
            JsonNode node = MAPPER.readTree(body == null ? "" : body);
            int status = node.path("status").asInt(-1);
            String msg = node.path("msg").asText("");
            if (status != 200) {
                throw new DefaultClientException("下发失败：" + (StringUtil.isNotBlank(msg) ? msg : ("status=" + status)));
            }
        } catch (DefaultClientException e) {
            throw e;
        } catch (Exception e) {
            throw new DefaultClientException("下发响应解析失败");
        }

        // 更新任务为已下发
        MachineTaskMagneticPowder update = new MachineTaskMagneticPowder();
        update.setId(task.getId());
        update.setMachineTaskStatus(1);
        update.setSendTime(LocalDateTime.now());
        this.updateById(update);
    }

    @Override
    public List<RemoteFolderDto> getRemoteFolders() {
        // 查询磁粉机设备（machineType = 2，仅一条）
        MachineInfo machine = machineInfoService.lambdaQuery()
                .eq(MachineInfo::getMachineType, 2)
                .last("limit 1")
                .one();
        if (machine == null || StringUtil.isBlank(machine.getIpAddress())) {
            throw new DefaultClientException("未配置磁粉机设备IP地址！");
        }

        // 调用远程 http://{ip}/api/folders 获取文件夹数组
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        String url = "http://" + machine.getIpAddress() + "/api/folders";
        log.info("获取远程磁粉机文件夹列表，请求url={}", url);
        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(url, String.class);
        } catch (ResourceAccessException e) {
            log.warn("获取远程磁粉机文件夹网络异常：{}", e.getMessage());
            throw new DefaultClientException("磁粉机网络连接错误");
        } catch (Exception e) {
            log.warn("获取远程磁粉机文件夹异常：{}", e.getMessage(), e);
            throw new DefaultClientException("获取失败");
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new DefaultClientException("获取失败：" + response.getStatusCode());
        }

        String body = response.getBody();
        if (StringUtil.isBlank(body)) {
            return new ArrayList<>();
        }
        try {
            JsonNode node = MAPPER.readTree(body);
            if (!node.isArray()) {
                throw new DefaultClientException("响应格式错误");
            }
            List<RemoteFolderDto> result = new ArrayList<>();
            for (JsonNode item : node) {
                RemoteFolderDto dto = new RemoteFolderDto();
                dto.setName(item.path("name").asText(""));
                dto.setPath(item.path("path").asText(""));
                dto.setCtime(item.path("ctime").isNumber() ? item.get("ctime").asLong() : null);
                dto.setMtime(item.path("mtime").isNumber() ? item.get("mtime").asLong() : null);
                dto.setCount(item.path("count").isNumber() ? item.get("count").asInt() : null);
                result.add(dto);
            }
            return result;
        } catch (DefaultClientException e) {
            throw e;
        } catch (Exception e) {
            log.warn("远程文件夹响应解析失败：{}", e.getMessage(), e);
            throw new DefaultClientException("响应解析失败");
        }
    }

    @Override
    public List<RemoteFileDto> getRemoteFiles(String folder) {
        if (StringUtil.isBlank(folder)) {
            throw new DefaultClientException("folder不能为空！");
        }
        // 查询磁粉机设备（machineType = 2，仅一条）
        MachineInfo machine = machineInfoService.lambdaQuery()
                .eq(MachineInfo::getMachineType, 2)
                .last("limit 1")
                .one();
        if (machine == null || StringUtil.isBlank(machine.getIpAddress())) {
            throw new DefaultClientException("未配置磁粉机设备IP地址！");
        }

        // 调用远程 http://{ip}/api/files?folder={folder}
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        String url = UriComponentsBuilder
                .fromHttpUrl("http://" + machine.getIpAddress() + "/api/files")
                .queryParam("folder", folder)
                .build()
                .encode()
                .toUriString();
        log.info("获取远程磁粉机文件列表，请求url={}", url);
        ResponseEntity<String> response;
        try {
            response = restTemplate.getForEntity(url, String.class);
        } catch (ResourceAccessException e) {
            log.warn("获取远程磁粉机文件列表网络异常：{}", e.getMessage());
            throw new DefaultClientException("磁粉机网络连接错误");
        } catch (Exception e) {
            log.warn("获取远程磁粉机文件列表异常：{}", e.getMessage(), e);
            throw new DefaultClientException("获取失败");
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new DefaultClientException("获取失败：" + response.getStatusCode());
        }

        String body = response.getBody();
        if (StringUtil.isBlank(body)) {
            return new ArrayList<>();
        }
        try {
            JsonNode node = MAPPER.readTree(body);
            if (!node.isArray()) {
                throw new DefaultClientException("响应格式错误");
            }
            List<RemoteFileDto> result = new ArrayList<>();
            for (JsonNode item : node) {
                RemoteFileDto dto = new RemoteFileDto();
                dto.setName(item.path("name").asText(""));
                dto.setPath(item.path("path").asText(""));
                if (item.has("ann")) {
                    dto.setAnn(item.get("ann").asBoolean(false));
                } else {
                    dto.setAnn(null);
                }
                dto.setSize(item.path("size").isNumber() ? item.get("size").asLong() : null);
                dto.setMtime(item.path("mtime").isNumber() ? item.get("mtime").asLong() : null);
                result.add(dto);
            }
            return result;
        } catch (DefaultClientException e) {
            throw e;
        } catch (Exception e) {
            log.warn("远程文件列表响应解析失败：{}", e.getMessage(), e);
            throw new DefaultClientException("响应解析失败");
        }
    }

    @Override
    public ResponseEntity<byte[]> getRemoteImage(String path, Boolean overlay, Boolean thumb) {
        if (StringUtil.isBlank(path)) {
            throw new DefaultClientException("path不能为空！");
        }
        // 查询磁粉机设备（machineType = 2，仅一条）
        MachineInfo machine = machineInfoService.lambdaQuery()
                .eq(MachineInfo::getMachineType, 2)
                .last("limit 1")
                .one();
        if (machine == null || StringUtil.isBlank(machine.getIpAddress())) {
            throw new DefaultClientException("未配置磁粉机设备IP地址！");
        }

        // 使用 UriComponentsBuilder 安全构建 URL，避免编码问题
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("http://" + machine.getIpAddress() + "/api/image")
                .queryParam("path", path);
        if (overlay != null) {
            builder.queryParam("overlay", overlay);
        }
        if (thumb != null) {
            builder.queryParam("thumb", thumb);
        }
        String url = builder.toUriString();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(10000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        HttpHeaders headers = new HttpHeaders();
        // 透传期望图片类型，但远端会按其实际返回
        headers.setAccept(Collections.singletonList(MediaType.ALL));
        HttpEntity<Void> req = new HttpEntity<>(headers);

        log.info("代理获取远程磁粉机图片，请求url={}", url);
        ResponseEntity<byte[]> resp;
        try {
            resp = restTemplate.exchange(url, HttpMethod.GET, req, byte[].class);
        } catch (ResourceAccessException e) {
            log.warn("获取远程磁粉机图片网络异常：{}", e.getMessage());
            throw new DefaultClientException("磁粉机网络连接错误");
        } catch (Exception e) {
            log.warn("获取远程磁粉机图片异常：{}", e.getMessage(), e);
            throw new DefaultClientException("获取失败");
        }
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new DefaultClientException("获取失败：" + resp.getStatusCode());
        }

        // 直接将状态码、响应头与字节数组返回给前端，以便浏览器预览
        HttpHeaders outHeaders = new HttpHeaders();
        MediaType contentType = resp.getHeaders().getContentType();
        if (contentType != null) {
            outHeaders.setContentType(contentType);
        } else {
            // 回退为通用图片类型
            outHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        List<String> cd = resp.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION);
        if (cd != null && !cd.isEmpty()) {
            outHeaders.put(HttpHeaders.CONTENT_DISPOSITION, cd);
        }
        Long cl = resp.getHeaders().getContentLength();
        if (cl != null && cl >= 0) {
            outHeaders.setContentLength(cl);
        }
        return new ResponseEntity<>(resp.getBody(), outHeaders, resp.getStatusCode());
    }
}



