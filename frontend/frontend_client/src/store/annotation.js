import { defineStore } from "pinia";
import { ref, watch, nextTick } from "vue";
import AnnotationService from "../services/annotation.service";

export const useAnnotationStore = defineStore("annotation", () => {
  const annotationObj = ref({});
  const annotationSelected = ref({});
  // const typingNewNote = ref(false);

  let expansions = ref([]);
  expansions.value = Object.keys(annotationObj.value);

  let annotationObjView = ref({});
  annotationObjView.value = { ...annotationObj.value };

  const pdfPageWidth = ref(-1),
    pdfPageHeight = ref(-1);

  watch(
    annotationObj,
    () => {
      // console.log("Change: annotationObj")
      expansions.value = [...Object.keys(annotationObj.value)];
      annotationObjView.value = JSON.parse(JSON.stringify(annotationObj.value));
    },
    { deep: true }
  );

  watch(
    annotationSelected,
    async () => {
      // console.log("annotationSelected")
      if (annotationSelected.value?.id) {
        if (
          !expansions.value.find(
            (e) =>
              parseInt(e) - 1 ==
              annotationSelected.value.target.selector.node.index
          )
        ) {
          expansions.value.push(
            (
              parseInt(annotationSelected.value.target.selector.node.index) + 1
            ).toString()
          );
          await nextTick();
        }
        let elm = document.getElementById(annotationSelected.value.id);
        elm?.scrollIntoView(true);
        setTimeout(() => {
          elm?.scrollIntoView(true);
        }, 50);
      }
    },
    { deep: true }
  );

  function $reset() {
    annotationObj.value = {}
    annotationSelected.value = {}
  }

  async function updateAnnotation(annotation, idx = -1) {
    try {
      const res = await AnnotationService.updateAnnotation(annotation);
      // alert("annotaion.js: updateAnnotation")
      let page = parseInt(annotation.target.selector.node.index) + 1;
      if (idx === -1) {
        const length = annotationObj.value[page].length;
        for (let i = 0; i < length; i++) {
          if (annotationObj.value[page][i].id === annotation.id) {
            // annotationObj.value[page][i].bodyValue = annotation.bodyValue;
            // annotationObj.value[page][i].target.selector.strokeColor =
            //   annotation.target.selector.strokeColor;
            annotationObj.value[page][i] = {...annotation}
            break;
          }
        }
      } else {
        // annotationObj.value[page][idx].bodyValue = annotation.bodyValue;
        // annotationObj.value[page][idx].target.selector.strokeColor =
        //   annotation.target.selector.strokeColor;
        annotationObj.value[page][idx] = {...annotation}
      }
    } catch (error) {
      console.log(error);
    }
  }

  async function deleteAnnotation(annotation, idx = -1) {
    try {
      const res = await AnnotationService.deleteAnnotation(annotation.id);
      let page = parseInt(annotation.target.selector.node.index) + 1;
      const length = annotationObj.value[page].length;
      if (idx === -1) {
        for (let i = 0; i < length; i++) {
          if (annotationObj.value[page][i].id === annotation.id) {
            annotationObj.value[page].splice(i, 1);
            break;
          }
        }
      } else {
        annotationObj.value[page].splice(idx, 1);
      }

      if (annotationObj.value[page].length === 0)
        delete annotationObj.value[page];
    } catch (error) {
      console.log(error);
    }
  }

  // async function addedAnnotation(annotation) {
  //   try {
  //     const res = await AnnotationService.addNewAnnotation(annotation);
  //     let page = parseInt(annotation.target.selector.node.index) + 1;
  //     if (!annotationObj.value[page]) annotationObj.value[page] = [];
  //     annotationObj.value[page].push(annotation);
  //     // await nextTick();
  //     // const elm = document.getElementById(annotation.id);
  //     // if (elm) {
  //     //   elm.click();
  //     //   elm.querySelector(`textarea[class="v-field__input"]`)?.focus();
  //     // }
  //   } catch (error) {
  //     console.log(error);
  //   }
  // }

  // let tempt,
  //   zoomLevel = -1,
  //   fileId,
  //   annotationManager;
  // function typeNewNote(data, fileID, annoManager) {
  //   typingNewNote.value = true;
  //   zoomLevel = parseFloat(data.zoomLevel);
  //   fileId = fileID;
  //   annotationManager = annoManager;
  //   tempt = data;
  // }

  // async function addNewNote(note) {
  //   let boundingBox = [1000000, -1, -1, 1000000],
  //     quadPoints = [],
  //     bboxs = tempt[`page${tempt.startPage}`],
  //     keys = Object.keys(bboxs),
  //     leng = keys.length - 1;

  //   // console.log(tempt, note, annotationManager, bboxs)
  //   for (let i = 0; i < leng; i++) {
  //     let boundingBoxTempt = [],
  //       xMin = parseFloat(bboxs[keys[i]].deviceLeft) / zoomLevel,
  //       yMax =
  //         pdfPageHeight.value -
  //         parseFloat(bboxs[keys[i]].deviceTop) / zoomLevel,
  //       xMax = parseFloat(bboxs[keys[i]].deviceRight) / zoomLevel,
  //       yMin =
  //         pdfPageHeight.value -
  //         parseFloat(bboxs[keys[i]].deviceBottom) / zoomLevel;

  //     boundingBoxTempt.push(xMin, yMax, xMax, yMin);

  //     for (let j = 0; j < boundingBoxTempt.length; j++) {
  //       if (j === 0 || j === boundingBoxTempt.length - 1) {
  //         if (boundingBox[j] > boundingBoxTempt[j]) {
  //           boundingBox[j] = boundingBoxTempt[j];
  //         }
  //       } else {
  //         if (boundingBox[j] < boundingBoxTempt[j]) {
  //           boundingBox[j] = boundingBoxTempt[j];
  //         }
  //       }
  //     }

  //     quadPoints.push(
  //       boundingBoxTempt[0],
  //       boundingBoxTempt[1],
  //       boundingBoxTempt[2],
  //       boundingBoxTempt[1],
  //       boundingBoxTempt[0],
  //       boundingBoxTempt[3],
  //       boundingBoxTempt[2],
  //       boundingBoxTempt[3]
  //     );
  //   }

  //   const t = Date.now().toString(36) + Math.random().toString(36).substr(2);

  //   let annotation = {
  //     id: `a2-${t}-${t}`,
  //     created: new Date().toJSON(),
  //     modified: new Date().toJSON(),
  //     "@context": [
  //       "https://www.w3.org/ns/anno.jsonld",
  //       "https://comments.acrobat.com/ns/anno.jsonld",
  //     ],
  //     type: "Annotation",
  //     motivation: "commenting",
  //     bodyValue: note,
  //     creator: {
  //       id: "Guest",
  //       name: "Guest",
  //       type: "Person",
  //     },
  //     target: {
  //       source: fileId,
  //       selector: {
  //         node: { index: tempt.startPage },
  //         quadPoints,
  //         boundingBox,
  //         opacity: 1,
  //         subtype: "highlight",
  //         strokeColor: "#F8D147",
  //         type: "AdobeAnnoSelector",
  //       },
  //     },
  //   };

  //   console.log("addAnnotations", annotation);

  //   annotationManager
  //     .addAnnotations([annotation])
  //     .then(() => {
  //       typingNewNote.value = false;
  //       return true;
  //     })
  //     .catch((error) => {
  //       console.log(error);
  //       typingNewNote.value = false;
  //     });

  //   return false;
  // }

  return {
    annotationObj,
    annotationObjView,
    annotationSelected,
    expansions,
    pdfPageWidth,
    pdfPageHeight,
    // typingNewNote,
    // addNewNote,
    // typeNewNote,
    updateAnnotation,
    deleteAnnotation,
    $reset,
    // addedAnnotation,
  };
});
