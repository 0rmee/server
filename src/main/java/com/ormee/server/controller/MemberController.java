package com.ormee.server.controller;

import com.ormee.server.dto.member.FindPasswordDto;
import com.ormee.server.dto.member.FindUsernameDto;
import com.ormee.server.dto.response.ResponseDto;
import com.ormee.server.service.MemberService;
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
