package im.zhaojun.controller;

import com.github.pagehelper.PageInfo;
import im.zhaojun.annotation.OperationLog;
import im.zhaojun.model.User;
import im.zhaojun.service.RoleService;
import im.zhaojun.service.UserService;
import im.zhaojun.util.PageResultBean;
import im.zhaojun.util.ResultBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RoleService roleService;

    @GetMapping("/index")
    public String index() {
        return "user/user-list";
    }

    @OperationLog("获取用户列表")
    @GetMapping("/list")
    @ResponseBody
    public PageResultBean<User> getList(@RequestParam(value = "page", defaultValue = "1") int page,
                                          @RequestParam(value = "limit", defaultValue = "10")int limit) {
        List<User> users = userService.selectAllWithDept(page, limit);
        PageInfo<User> userPageInfo = new PageInfo<>(users);
        return new PageResultBean<>(userPageInfo.getTotal(), userPageInfo.getList());
    }

    @GetMapping
    public String add(Model model) {
        model.addAttribute("roles", roleService.selectAll());
        return "user/user-add";
    }

    @GetMapping("/{userId}")
    public String edit(@PathVariable("userId") Integer userId, Model model) {
        model.addAttribute("roleIds", userService.selectRoleIdsById(userId));
        model.addAttribute("user", userService.selectOne(userId));
        model.addAttribute("roles", roleService.selectAll());
        return "user/user-add";
    }

    @OperationLog("编辑角色")
    @PutMapping
    @ResponseBody
    public ResultBean edit(User user, @RequestParam(value = "role[]", required = false) Integer roleIds[]) {
        userService.update(user, roleIds);
        return ResultBean.success();
    }

    @OperationLog("新增用户")
    @PostMapping
    @ResponseBody
    public ResultBean add(@Valid User user, @RequestParam(value = "role[]", required = false) Integer roleIds[]) {
        return ResultBean.success(userService.add(user, roleIds));
    }

    @OperationLog("禁用账号")
    @PostMapping("/{userId}/disable")
    @ResponseBody
    public ResultBean disable(@PathVariable("userId") Integer userId) {
        return ResultBean.success(userService.disableUserByID(userId));
    }

    @OperationLog("激活账号")
    @PostMapping("/{userId}/enable")
    @ResponseBody
    public ResultBean enable(@PathVariable("userId") Integer userId) {
        return ResultBean.success(userService.enableUserByID(userId));
    }

    @OperationLog("删除账号")
    @DeleteMapping("/{userId}")
    @ResponseBody
    public ResultBean delete(@PathVariable("userId") Integer userId) {
        userService.delete(userId);
        return ResultBean.success();
    }

    @GetMapping("/{userId}/reset")
    public String resetPassword(@PathVariable("userId") Integer userId, Model model) {
        model.addAttribute("userId", userId);
        return "user/user-reset-pwd";
    }

    @OperationLog("重置密码")
    @PostMapping("/{userId}/reset")
    @ResponseBody
    public ResultBean resetPassword(@PathVariable("userId") Integer userId, String password) {
        userService.updatePasswordByUserId(userId, password);
        return ResultBean.success();
    }

}