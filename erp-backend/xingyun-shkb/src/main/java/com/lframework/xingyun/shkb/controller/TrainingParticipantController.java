package com.lframework.xingyun.shkb.controller;

import com.lframework.starter.web.core.annotations.security.HasPermission;
import com.lframework.starter.web.core.controller.DefaultBaseController;
import com.lframework.starter.web.core.components.resp.InvokeResult;
import com.lframework.starter.web.core.components.resp.InvokeResultBuilder;
import com.lframework.starter.web.core.components.resp.PageResult;
import com.lframework.xingyun.shkb.bo.participant.GetTrainingParticipantBo;
import com.lframework.xingyun.shkb.bo.participant.QueryTrainingParticipantBo;
import com.lframework.xingyun.shkb.service.ShkbTrainingParticipantService;
import com.lframework.xingyun.shkb.vo.participant.CreateTrainingParticipantVo;
import com.lframework.xingyun.shkb.vo.participant.QueryTrainingParticipantVo;
import com.lframework.xingyun.shkb.vo.participant.UpdateTrainingParticipantVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(
   tags = {"培训学员管理"}
)
@RestController
@RequestMapping({"/training-participant"})
@Validated
public class TrainingParticipantController extends DefaultBaseController {
   @Autowired
   private ShkbTrainingParticipantService participantService;

   @ApiOperation("查询学员列表")
   @HasPermission({"hr:training:query"})
   @PostMapping({"/query"})
   public InvokeResult<PageResult<QueryTrainingParticipantBo>> query(@Valid @RequestBody QueryTrainingParticipantVo vo) {
      PageResult<QueryTrainingParticipantBo> pageResult = this.participantService.query(this.getPageIndex(vo), this.getPageSize(vo), vo.getImplementationId());
      return InvokeResultBuilder.success(pageResult);
   }

   @ApiOperation("查询学员列表（不分页）")
   @HasPermission({"hr:training:query"})
   @GetMapping({"/list/{implementationId}"})
   public InvokeResult<List<QueryTrainingParticipantBo>> getList(@ApiParam("培训实施ID") @PathVariable String implementationId) {
      PageResult<QueryTrainingParticipantBo> pageResult = this.participantService.query(1, 9999, implementationId);
      return InvokeResultBuilder.success(pageResult.getDatas());
   }

   @ApiOperation("获取学员详情")
   @HasPermission({"hr:training:query"})
   @GetMapping({"/{id}"})
   public InvokeResult<GetTrainingParticipantBo> get(@ApiParam("学员ID") @NotBlank(message = "学员ID不能为空") @PathVariable String id) {
      GetTrainingParticipantBo bo = this.participantService.getDetail(id);
      return InvokeResultBuilder.success(bo);
   }

   @ApiOperation("创建学员")
   @HasPermission({"hr:training:create"})
   @PostMapping
   public InvokeResult<Void> create(@Valid @RequestBody CreateTrainingParticipantVo vo) {
      this.participantService.create(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("批量创建学员")
   @HasPermission({"hr:training:create"})
   @PostMapping({"/batch"})
   public InvokeResult<Void> createBatch(@Valid @RequestBody List<CreateTrainingParticipantVo> voList) {
      for (CreateTrainingParticipantVo vo : voList) {
         this.participantService.create(vo);
      }

      return InvokeResultBuilder.success();
   }

   @ApiOperation("更新学员")
   @HasPermission({"hr:training:update"})
   @PutMapping
   public InvokeResult<Void> update(@Valid @RequestBody UpdateTrainingParticipantVo vo) {
      this.participantService.update(vo);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("删除学员")
   @HasPermission({"hr:training:delete"})
   @DeleteMapping({"/{id}"})
   public InvokeResult<Void> delete(@ApiParam("学员ID") @NotBlank(message = "学员ID不能为空") @PathVariable String id) {
      this.participantService.deleteById(id);
      return InvokeResultBuilder.success();
   }

   @ApiOperation("批量删除学员")
   @HasPermission({"hr:training:delete"})
   @DeleteMapping({"/batch"})
   public InvokeResult<Void> deleteBatch(@RequestBody List<String> ids) {
      for (String id : ids) {
         this.participantService.deleteById(id);
      }

      return InvokeResultBuilder.success();
   }
}
