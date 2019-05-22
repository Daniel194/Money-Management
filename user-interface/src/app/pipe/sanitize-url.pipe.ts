import { Pipe } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Pipe({
    name: "sanitizeUrl"
})
export class SanitizeUrlPipe {
    constructor(private sanitizer: DomSanitizer) { }

    transform(url: string): SafeResourceUrl {
        return this.sanitizer.bypassSecurityTrustResourceUrl(url);
    }
}
