class PdfViewer {
    constructor() {
        this.adobeDCView = null
        this.previewFilePromise = null

        this.viewerApis = null
        this.annotationManager = null

        this.pdfTitle = ""
        this.contentPdfFile = null
        this.currentPage = 0
        this.numPages = 0

        this.searchObject = null
    }
}