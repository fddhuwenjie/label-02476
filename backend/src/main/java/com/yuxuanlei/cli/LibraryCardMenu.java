package com.yuxuanlei.cli;

import com.yuxuanlei.entity.LibraryCard;
import com.yuxuanlei.service.LibraryCardService;
import com.yuxuanlei.util.PageResult;

import java.util.Scanner;

/**
 * 借书证管理菜单
 */
public class LibraryCardMenu {
    
    private final LibraryCardService libraryCardService;
    private final Scanner scanner = new Scanner(System.in);
    
    public LibraryCardMenu(LibraryCardService libraryCardService) {
        this.libraryCardService = libraryCardService;
    }
    
    public void show() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1":
                        issueCard();
                        break;
                    case "2":
                        renewCard();
                        break;
                    case "3":
                        updateStatus();
                        break;
                    case "4":
                        listCards();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("无效选项！");
                }
            } catch (Exception e) {
                System.out.println(CliErrorHandler.getUserFriendlyMessage(e));
            }
        }
    }
    
    private void issueCard() {
        System.out.println("\n=== 办理借书证（1:1 级联） ===");
        System.out.print("学生ID：");
        Long studentId = Long.parseLong(scanner.nextLine().trim());
        System.out.print("借书证号：");
        String cardNo = scanner.nextLine().trim();
        
        LibraryCard card = new LibraryCard();
        card.setStudentId(studentId);
        card.setCardNo(cardNo);
        
        Long id = libraryCardService.issueCard(card);
        System.out.println("✓ 办理成功！借书证ID：" + id);
    }
    
    private void renewCard() {
        System.out.println("\n=== 续期借书证 ===");
        System.out.print("借书证ID：");
        Long id = Long.parseLong(scanner.nextLine().trim());
        System.out.print("续期年数：");
        int years = Integer.parseInt(scanner.nextLine().trim());
        
        libraryCardService.renewCard(id, years);
        System.out.println("✓ 续期成功！");
    }
    
    private void updateStatus() {
        System.out.println("\n=== 更新借书证状态 ===");
        System.out.print("借书证ID：");
        Long id = Long.parseLong(scanner.nextLine().trim());
        System.out.println("状态选项：ACTIVE-有效 | EXPIRED-过期 | SUSPENDED-挂失");
        System.out.print("新状态：");
        String status = scanner.nextLine().trim();
        
        libraryCardService.updateCardStatus(id, status);
        System.out.println("✓ 更新成功！");
    }
    
    private void listCards() {
        System.out.println("\n=== 借书证列表（分页） ===");
        System.out.print("页码（默认1）：");
        String pageStr = scanner.nextLine().trim();
        int pageNum = pageStr.isEmpty() ? 1 : Integer.parseInt(pageStr);
        
        PageResult<LibraryCard> page = libraryCardService.listCards(pageNum, 10);
        
        System.out.println("\n总记录数：" + page.getTotal() + " | 当前页：" + page.getCurrent() + "/" + page.getPages());
        System.out.println("----------------------------------------");
        page.getRecords().forEach(card -> {
            System.out.println("ID:" + card.getId() + " | 学生ID:" + card.getStudentId() 
                    + " | 证号:" + card.getCardNo() + " | 状态:" + card.getStatus());
        });
    }
    
    private void printMenu() {
        System.out.println("\n========================================");
        System.out.println("    借书证管理");
        System.out.println("========================================");
        System.out.println("1. 办理借书证（1:1 级联）");
        System.out.println("2. 续期借书证");
        System.out.println("3. 更新借书证状态");
        System.out.println("4. 借书证列表（分页）");
        System.out.println("0. 返回主菜单");
        System.out.println("========================================");
        System.out.print("请选择：");
    }
}
