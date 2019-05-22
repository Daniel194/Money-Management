import {Component, OnInit} from '@angular/core';
import {AuthenticationService} from "../service/authentication.service";
import {Router} from "@angular/router";
import {StatisticsService} from "../service/statistics.service";
import {DataPoint} from "../domain/DataPoint";
import {DateFormatPipe} from "../pipe/date-format.pipe";
import {ItemMetric} from "../domain/ItemMetric";

@Component({
    selector: 'app-statistics',
    templateUrl: './statistics.component.html',
    styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent implements OnInit {

    public lineChartView: number[] = [900, 400];
    public pieChartView: number[] = [600, 400];

    public startDate: Date;
    public endDate: Date;

    public colorScheme = {
        domain: ['#5AA454', '#A10A28', '#C7B42C', '#AAAAAA']
    };

    public lineChartResults: any[] = [];
    public incomesPieChartResults: any[] = [];
    public expensePieChartResults: any[] = [];

    private datePoints: DataPoint[];

    constructor(private authService: AuthenticationService, private router: Router, private statisticsService: StatisticsService,
                private dateFormatPipe: DateFormatPipe) {
    }

    ngOnInit() {
        this.authService.checkCredentials();
        this.statisticsService.getCurrentAccountStatistics().subscribe(results => {
            this.datePoints = results;
            this.populateCharts(results);
        });
        this.initDates();
    }

    public logout() {
        this.authService.logout();
    }

    public navigateToAccount() {
        this.router.navigate(['/account']);
    }

    public navigateToSettings() {
        this.router.navigate(['/settings']);
    }

    public onSelect(event) {
        this.datePoints.forEach(point => {
            let date = this.dateFormatPipe.transform(point.id.date);

            if (event.name == date) {
                this.populatePieCharts(point);
                return;
            }
        })
    }

    public search() {
        this.statisticsService.getAccountStatisticsBetweenDates(this.startDate, this.endDate)
            .subscribe(results => {
                this.datePoints = results;
                this.populateCharts(results);
            });
    }

    private populateCharts(results: DataPoint[]) {
        let incomeResults: any[] = [];
        let expensesResults: any[] = [];
        let incomesPieChartResults: Map<String, number> = new Map();
        let expensesPieChartResults: Map<String, number> = new Map();

        results.forEach(result => {
            this.extractLineChartData(result, incomeResults, expensesResults);
            this.extractPieChartData(incomesPieChartResults, result.incomes);
            this.extractPieChartData(expensesPieChartResults, result.expenses);
        });

        this.lineChartResults = this.getLineChartData(incomeResults, expensesResults);
        this.incomesPieChartResults = this.getPieChartData(incomesPieChartResults);
        this.expensePieChartResults = this.getPieChartData(expensesPieChartResults);
    }

    private populatePieCharts(result: DataPoint) {
        let incomesPieChartResults: Map<String, number> = new Map();
        let expensesPieChartResults: Map<String, number> = new Map();

        this.extractPieChartData(incomesPieChartResults, result.incomes);
        this.extractPieChartData(expensesPieChartResults, result.expenses);

        this.incomesPieChartResults = this.getPieChartData(incomesPieChartResults);
        this.expensePieChartResults = this.getPieChartData(expensesPieChartResults);
    }

    private extractPieChartData(pieChartResults: Map<String, number>, items: Set<ItemMetric>) {
        items.forEach(income => {
            if (pieChartResults.has(income.title)) {
                let newAmount = pieChartResults.get(income.title) + Math.trunc(income.amount);
                pieChartResults.set(income.title, newAmount);
            } else {
                pieChartResults.set(income.title, Math.trunc(income.amount));
            }
        });
    }

    private extractLineChartData(result: DataPoint, incomeResults: any[], expensesResults: any[]) {
        let date = this.dateFormatPipe.transform(result.id.date);

        incomeResults.push({
            name: date,
            value: result.statistics["INCOMES_AMOUNT"]
        });

        expensesResults.push({
            name: date,
            value: result.statistics["EXPENSES_AMOUNT"]
        });
    }

    private getLineChartData(incomeResults: any[], expensesResults: any[]): any[] {
        return [
            {
                name: 'Incomes',
                series: incomeResults
            },
            {
                name: 'Expenses',
                series: expensesResults
            }
        ];
    }

    private getPieChartData(pieChartResults: Map<String, number>): any[] {
        let pieChartResultsArray = this.convertPieChartMapToArray(pieChartResults);
        pieChartResultsArray.sort((a, b) => b.value - a.value);

        if (pieChartResultsArray.length > 4) {
            return this.shrinkPieChartArray(pieChartResultsArray);
        } else {
            return pieChartResultsArray;
        }
    }

    private convertPieChartMapToArray(pieChartResults: Map<String, number>): any[] {
        let pieChartResultsArray = [];

        pieChartResults.forEach((v, k) => {
            pieChartResultsArray.push({
                "name": k,
                "value": v
            });
        });

        return pieChartResultsArray;
    }

    private shrinkPieChartArray(pieChartResultsArray: any[]): any[] {
        let others = {
            "name": "Others",
            "value": 0
        };

        pieChartResultsArray.slice(3, pieChartResultsArray.length).forEach(result => {
            others.value += result.value;
        });

        let shrinkArray = pieChartResultsArray.slice(0, 3);
        shrinkArray.push(others);

        return shrinkArray;
    }

    private initDates() {
        let date = new Date();
        this.startDate = new Date(date.getFullYear(), date.getMonth(), 1);
        this.endDate = new Date(date.getFullYear(), date.getMonth() + 1, 0);
    }

}
