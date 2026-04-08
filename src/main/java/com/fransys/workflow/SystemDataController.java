package com.fransys.workflow;

import com.fransys.common.api.ApiResponse;
import com.fransys.dict.DictItem;
import com.fransys.enterprise.Enterprise;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SystemDataController {

    private final SystemDataService systemDataService;

    @GetMapping("/api/dicts/grouped")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SALES','ROLE_OPERATIONS')")
    public ApiResponse<Map<String, List<DictItem>>> groupedDicts() {
        return ApiResponse.success(systemDataService.groupedDicts());
    }

    @GetMapping("/api/dicts")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<List<DictItem>> dictItems() {
        return ApiResponse.success(systemDataService.listDictItems());
    }

    @PostMapping("/api/dicts")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<DictItem> saveDictItem(@RequestBody DictItem item) {
        return ApiResponse.success(systemDataService.saveDictItem(item));
    }

    @GetMapping("/api/enterprises")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_OPERATIONS','ROLE_SALES')")
    public ApiResponse<List<Enterprise>> enterprises() {
        return ApiResponse.success(systemDataService.listEnterprises());
    }

    @PostMapping("/api/enterprises")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_OPERATIONS')")
    public ApiResponse<Enterprise> saveEnterprise(@RequestBody Enterprise enterprise) {
        return ApiResponse.success(systemDataService.saveEnterprise(enterprise));
    }

    @GetMapping("/api/system/meta")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<WorkflowDtos.SystemMetaResponse> systemMeta() {
        return ApiResponse.success(systemDataService.systemMeta());
    }

    @PostMapping("/api/system/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ApiResponse<WorkflowDtos.UserSummary> saveUser(@Valid @RequestBody WorkflowDtos.UserUpsertRequest request) {
        return ApiResponse.success(systemDataService.saveUser(request));
    }
}
