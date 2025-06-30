package com.ormee.server.member.controller;

import com.ormee.server.member.service.MemberService;
import com.ormee.server.member.dto.FindPasswordDto;
import com.ormee.server.member.dto.FindUsernameDto;
import com.ormee.server.global.response.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/username")
    public ResponseDto findUsername(@RequestBody FindUsernameDto findUsernameDto) {
        return ResponseDto.success(memberService.findUsername(findUsernameDto));
    }

    @PutMapping("/password")
    public ResponseDto findPassword(@RequestBody FindPasswordDto findPasswordDto) {
        memberService.findPassword(findPasswordDto);
        return ResponseDto.success();
    }
}
