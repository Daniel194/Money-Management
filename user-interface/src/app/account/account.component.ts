import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../service/authentication.service";
import {Router} from "@angular/router";
import {MatDialog, MatDialogRef} from "@angular/material";
import {ItemDialogComponent} from "../item-dialog/item-dialog.component";
import {AccountService} from "../service/account.service";
import {Account} from "../domain/Account";
import {Item} from "../domain/Item";
import {ToastrService} from "ngx-toastr";
import * as $ from 'jquery';
import {IconService} from "../service/icon.service";

@Component({
    selector: 'app-account',
    templateUrl: './account.component.html',
    styleUrls: ['./account.component.css']
})
export class AccountComponent implements OnInit {

    public account: Account;

    constructor(private authService: AuthenticationService, private router: Router, public dialog: MatDialog,
                private accountService: AccountService, private toaster: ToastrService, public iconService: IconService) {
    }

    ngOnInit() {
        this.accountService.getCurrentAccount().subscribe(result => {
            this.account = result;

            if (this.account.incomes == null) {
                this.account.incomes = []
            }

            if (this.account.expenses == null) {
                this.account.expenses = []
            }

        })
    }

    public navigateToStatistics() {
        this.router.navigate(['/statistics'])
    }

    public openUpdateIncome(index: number) {
        let dialog = this.openItemDialog(this.account.incomes[index]);
        dialog.componentInstance.title = "Update Income";
        dialog.componentInstance.icons = IconService.getIncomeIcons();

        dialog.afterClosed().subscribe(result => {
            if (result != null) {
                this.account.incomes[index] = result;
            }
        });
    }

    public openUpdateExpense(index: number) {
        let dialog = this.openItemDialog(this.account.expenses[index]);
        dialog.componentInstance.title = "Update Expense";
        dialog.componentInstance.icons = IconService.getExpenseIcons();

        dialog.afterClosed().subscribe(result => {
            if (result != null) {
                this.account.expenses[index] = result;
            }
        });
    }

    public openAddIncome() {
        let dialog = this.openItemDialog(null);
        dialog.componentInstance.title = "Add Income";
        dialog.componentInstance.icons = IconService.getIncomeIcons();

        dialog.afterClosed().subscribe(result => {
            if (result != null) {
                this.account.incomes.push(result);
            }
        });
    }

    public openAddExpense() {
        let dialog = this.openItemDialog(null);
        dialog.componentInstance.title = "Add Expense";
        dialog.componentInstance.icons = IconService.getExpenseIcons();

        dialog.afterClosed().subscribe(result => {
            if (result != null) {
                this.account.expenses.push(result);
            }
        });
    }

    public saveAccount() {
        this.accountService.saveAccount(this.account).subscribe(() => {
                this.toaster.success('The account was successfully saved !', 'Success');
            },
            error => {
                this.toaster.error('An error occur during the saving !', 'Error');
            });
    }

    public incomeDown() {
        AccountComponent.down("income");
    }

    public incomeUp() {
        AccountComponent.up("income");
    }

    public expenseDown() {
        AccountComponent.down("expense");
    }

    public expenseUp() {
        AccountComponent.up("expense");
    }

    private static up(id: String) {
        var slider = $("#" + id + "slider");
        var sliderOffset = $(slider).position().top;
        var itemHeight = 70;
        var scroll = Math.abs(sliderOffset) + itemHeight;

        $('#' + id + 'frame').animate({
            scrollTop: scroll
        }, 'slow');
    }

    private static down(id: String) {
        var slider = $("#" + id + "slider");
        var sliderOffset = $(slider).position().top;
        var itemHeight = 70;

        if (sliderOffset < 0) {
            var lastElementOnScreen = Math.abs(Math.round(sliderOffset / itemHeight)) - 1;

            $('#' + id + 'frame').animate({
                scrollTop: lastElementOnScreen * itemHeight
            }, 'slow');
        }
    }

    private openItemDialog(item: Item): MatDialogRef<ItemDialogComponent, any> {
        return this.dialog.open(ItemDialogComponent, {
            width: '250px',
            data: item
        });
    }

}
