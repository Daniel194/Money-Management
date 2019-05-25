import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material";
import {Item} from "../domain/Item";
import {AuthenticationService} from "../service/authentication.service";
import {IconService} from "../service/icon.service";

@Component({
    selector: 'app-item-dialog',
    templateUrl: './item-dialog.component.html',
    styleUrls: ['./item-dialog.component.css']
})
export class ItemDialogComponent implements OnInit {

    public title: String = "";
    public icons: String[] = [];

    constructor(
        private authService: AuthenticationService, public iconService: IconService,
        public dialogRef: MatDialogRef<ItemDialogComponent>, @Inject(MAT_DIALOG_DATA) public item: Item) {
    }

    ngOnInit() {
        if (this.item == null) {
            this.initItem()
        }
    }

    onCloseClick(): void {
        this.dialogRef.close(null);
    }

    onAddClick() {
        this.dialogRef.close(this.item);
    }

    private initItem() {
        this.item = new Item();
        this.item.currency = 'EUR';
        this.item.period = 'MONTH';
        this.item.amount = '0';
        this.item.icon = '';
    }

}
