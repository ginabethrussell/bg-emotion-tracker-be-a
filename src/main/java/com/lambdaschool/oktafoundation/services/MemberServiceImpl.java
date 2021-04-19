package com.lambdaschool.oktafoundation.services;
import com.lambdaschool.oktafoundation.exceptions.ResourceNotFoundException;
import com.lambdaschool.oktafoundation.models.Member;
import com.lambdaschool.oktafoundation.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service(value = "memberService")
public class MemberServiceImpl implements MemberService
{
    @Autowired
    private MemberRepository memberRepository;
    @Override
    public List<Member> findAll()
    {
        List<Member> list = new ArrayList<>();
        memberRepository.findAll()
                .iterator()
                .forEachRemaining(list::add);
        return list;
    }

    @Transactional
    @Override
    public Member save(Member member)
    {
        Member newMember = new Member();
        newMember.setMemberid(member.getMemberid());
        memberRepository.save(newMember);
        return newMember;
    }

    @Transactional
    @Override
    public Member saveNewMember(String newMember)
    {
        Member addedMember = new Member();
        addedMember.setMemberid(newMember);
        memberRepository.save(addedMember);
        return addedMember;
    }

    @Transactional
    @Override
    public List<Member> saveNewMembers(InputStream stream) throws IOException
    {
        List<Member> newMembers = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String member;
        // removes header line from CSV file
        String headerLine = reader.readLine();
        while((member = reader.readLine())!= null)
        {
            // removes any quotes if needed from ends of memberid in CSV file
            member = member.replaceAll("^\"|\"$", "");

            Member isCurrentMember = memberRepository.findMemberByMemberid(member);
            if ( isCurrentMember == null )
            {
                Member newMember = new Member();
                newMember.setMemberid(member);
                Member addedMember = save(newMember);
                newMembers.add(addedMember);
            }

        }
        return newMembers;
    }

    @Override
    public Member findMemberByJavaId(long id)
    {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member id " + id + "Not Found"));
    }
    @Override
    public Member findMemberByStringId(String memberId)
    {
        Member mm = memberRepository.findMemberByMemberid(memberId);
        if (mm == null)
        {
            throw new ResourceNotFoundException("Member Id" + memberId + "Not Found");
        }
        return mm;
    }
    @Override
    public List<Member> findByIdContaining(String partialmemberId)
    {
        return memberRepository.findMembersByMemberidContaining(partialmemberId);
    }
    @Override
    public void delete(long id)
    {
        memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member id" + id + " Not Found"));
        memberRepository.deleteById(id);
    }
}