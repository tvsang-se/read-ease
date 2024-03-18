import { translate } from "@vitalets/google-translate-api";
import { HttpProxyAgent } from "http-proxy-agent";
import { fetchUrl } from "fetch";
import { JSDOM } from "jsdom";
import * as googleTTS from "google-tts-api";

// const params = {
//   q: "안녕하세요",
//   targetLanguage: "vi",
// };
// vi: Xin chào    [VN]
// en: Hello       [US]
// ko: 안녕하세요
// ja: こんにちは   [JP]
// zh: 你好         [CN]
// th: สวัสดี        [TH]

// translateFunc(params);
// getIpAndTranslate(params)

export async function translateFunc(req, res) {
  const q = req.body.q,
    targetLanguage = req.body.targetLanguage;
  try {
    const { text, raw } = await translate(q, { to: targetLanguage });
    // console.log(`[${raw.src}] ${q}\n[${targetLanguage}] ${text}`);
    // createAudioFile(q, raw.src);
    const urlAudios = googleTTS.getAllAudioUrls(q, {
      lang: raw.src,
      slow: false,
      host: "https://translate.google.com",
    });
    res.status(200).json({
      text,
      lang: raw.src,
      urlAudios: urlAudios.map((urlA) => urlA.url),
    });
  } catch (error) {
    if ((error.name = "TooManyRequestsError")) {
      console.log(error)
      await getIpAndTranslate(req, res);
    } else {
      console.log("[Error 1] ", error.name);
      res.status(500).json({
        message: "Fail to transtale !",
      });
    }
  }
}

export async function getIpAndTranslate(req, res) {
  const q = req.body.q,
    targetLanguage = req.body.targetLanguage;
  fetchUrl("https://free-proxy-list.net/", async function (error, meta, body) {
    const htmlString = body.toString();
    const dom = new JSDOM(htmlString);
    // console.log(htmlString);
    const data = dom.window.document.querySelectorAll(
      "table.table.table-striped.table-bordered tbody tr"
    );

    let s = [];
    for (let i = 0; i < data.length; i++) {
      if (
        data[i].querySelectorAll("td")[4].textContent === "anonymous" &&
        data[i].querySelectorAll("td")[5].textContent === "yes"
      ) {
        let temp = {};
        temp.ip = data[i].querySelectorAll("td")[0].textContent;
        temp.port = data[i].querySelectorAll("td")[1].textContent;

        // Convertion examples: "9 secs ago" => lastChecked: 9 (s)
        //                      "6 mins ago" => lastChecked: 6*60 = 360 (s)
        const tmp = data[i].querySelectorAll("td")[7].textContent.split(" ");
        temp.lastChecked = tmp[0] * 1.0; // convert: string -> float
        if (tmp[1].startsWith("m")) temp.lastChecked *= 60;
        else if (tmp[1].startsWith("h")) temp.lastChecked *= 3600;

        s.push(temp);
      }
    }

    s.sort((a, b) => {
      return a.lastChecked - b.lastChecked; // Ascending [ if(condition > 0) swap() ]
    });

    let i = 0,
      url;
    for (i = 0; i < s.length; i++) {
      url = `http://${s[i].ip}:${s[i].port}`;
      try {
        await translateByHttpProxy({
          q,
          targetLanguage,
          url,
          res,
        });
      } catch (error) {
        console.log(`${url} ==> Error`);
      }
    }
    if (i >= s.length) {
      console.log("Fail to transtale !!!");
      res.status(500).json({
        message: "Fail to transtale !",
      });
    }
  });
}

async function translateByHttpProxy({ q, targetLanguage, url, res }) {
  const agent = new HttpProxyAgent(url);
  const { text, raw } = await translate(q, {
    to: targetLanguage,
    fetchOptions: { agent },
  });
  //   console.log(`[${raw.src}] ${q}\n[${targetLanguage}] ${text} ==> Error`);
  // createAudioFile(q, raw.src);
  const urlAudios = googleTTS.getAllAudioUrls(q, {
    lang: raw.src,
    slow: false,
    host: "https://translate.google.com",
  });
  res
    .status(200)
    .json({
      text,
      lang: raw.src,
      urlAudios: urlAudios.map((urlA) => urlA.url),
    });
}

// modules.exports = {
//   translateFunc
// }
