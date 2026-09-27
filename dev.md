# BinMe ♻️ — Devpost

---

## Inspiration

Every year, nearly **300 million tons** of waste ends up in landfills across the U.S. alone — and studies show that **over 30%** of that waste was actually recyclable or compostable. The problem isn't apathy; it's **confusion**. We've all stood at a bin, staring at a greasy pizza box or a coffee cup sleeve, wondering: *does this go in recycling, compost, or trash?*

Living in **Lake Oswego and Clackamas County, Oregon**, we noticed how inconsistent waste sorting rules are — what's recyclable in one city might be landfill in the next. Municipal recycling guidelines are buried in hard-to-find PDFs and government websites, and there's no single tool that gives you an instant, context-aware answer.

We were inspired by the idea of turning **your phone's camera into a real-time waste classification tool**, combining the power of **Vision AI** with hyper-local municipal recycling data. We wanted to build something that doesn't just tell you *what* an item is — but tells you *exactly what to do with it* based on where you live. BinMe was born to make waste sorting effortless, intelligent, and even rewarding.

---

## What it does

**BinMe** is an AI-powered Android app that makes responsible waste disposal as easy as snapping a photo. Here's what it delivers:

- **📸 AI Camera Scanner** — Point your camera at any waste item (a bottle, a takeout container, an old battery) and BinMe uses multi-modal Vision AI to instantly classify it into one of six categories: *Recyclable*, *Compostable*, *Organic*, *E-Waste*, *Hazardous*, or *Landfill* — along with a confidence score, step-by-step disposal instructions, and an eco-tip.

- **🗺️ Interactive Disposal Map** — BinMe surfaces nearby public smart bins, recycling hubs, e-waste drop-off stations, and composting sites using a fully interactive **OpenStreetMap** integration. Tap a pin, see the hours, and get one-tap navigation directions.

- **💬 Eco AI Chat Assistant** — Ask BinMe anything about waste sorting, municipal rules, or sustainable living. It's context-aware, injected with official **Lake Oswego & Clackamas County recycling guidelines**, and cites official government sources in its answers.

- **📊 Impact Tracker & Eco Points** — Every scan earns you eco-points. Track your disposal history, view weekly analytics on waste diverted from landfills, and monitor your personal carbon footprint reduction over time.

- **💡 Daily Eco Tips & Municipal Rules** — Get daily actionable sustainability advice and localized municipal recycling rules you never knew existed.

- **🎨 Stunning Material 3 UI** — Immersive onboarding with Lottie animations, glassmorphism surfaces, dynamic light/dark theming, and fluid micro-interactions — all built in 100% Jetpack Compose.

---

## How we built it

BinMe is a fully native **Android** application written in **100% Kotlin** using **Jetpack Compose** and **Material Design 3**.

**Architecture:**
We followed **Clean Architecture** principles with a strict **MVVM + Unidirectional Data Flow (UDF)** pattern. The UI layer is pure Compose, the ViewModel layer manages state via Kotlin `StateFlow` and `SharedFlow`, and the data layer is split into repositories that abstract remote APIs and local storage.

**AI & Vision Pipeline:**
The core scanning feature uses **Android CameraX** to capture high-resolution frames. The image is scaled, compressed, and Base64-encoded, then dispatched through **Cloudflare Workers AI** (`CloudflareVisionRepository`) and the **Groq API** powered by `qwen/qwen3.8-27b` multi-modal vision models. We enforce structured JSON output (`response_format = json_object`) so that every response includes the classification category, confidence percentage, disposal instructions, and an eco-tip — making parsing deterministic and reliable.

**Resilient Edge & Cloud Failover:**
By leveraging **Cloudflare Workers AI** as an edge gateway alongside multi-model Groq API endpoints, BinMe ensures ultra-fast, low-latency vision inference. If external API requests fail or time out due to connectivity issues, BinMe gracefully falls back to a built-in **deterministic municipal rules engine** (`LocalRecyclingDatabase`) that classifies common items based on curated Lake Oswego and Clackamas County guidelines. Users always get an accurate answer.

**Mapping:**
We integrated **osmdroid** (OpenStreetMap) for an open-source, offline-capable, API-key-free mapping experience. Custom marker overlays plot disposal locations dynamically, and users can tap for details and launch native navigation.

**Local Storage:**
User preferences and onboarding state are persisted with **Jetpack DataStore Preferences**. Scan history is maintained in-memory via `StateFlow` with a singleton repository.

**Key Libraries & Services:**
`Cloudflare Workers AI` (`CloudflareVisionRepository`), `Groq Vision & LLM API` (`qwen/qwen3.8-27b`), `Retrofit 2` + `OkHttp` for networking, `Gson` for serialization, `Lottie Compose` for animations, `Coil Compose` for image loading, `Accompanist` for runtime permissions, `osmdroid` for mapping, and `Firebase` for backend infrastructure.

---

## Challenges we ran into

- **Multi-modal API reliability** — Getting consistent, well-structured JSON responses from the Vision LLM was tricky. Models would sometimes return markdown-wrapped JSON or omit fields. We iterated heavily on our system prompts and enforced `response_format = json_object` to lock down the output schema.

- **Base64 payload size** — Camera frames captured at full resolution produced massive Base64 strings that exceeded API payload limits and caused timeouts. We had to implement aggressive image compression and scaling pipelines to keep payloads under 500KB without losing classification accuracy.

- **osmdroid integration in Compose** — Since osmdroid is a traditional Android `View`, embedding it inside Jetpack Compose required careful use of `AndroidView` interop, lifecycle management, and state synchronization between Compose recomposition and the imperative MapView API.

- **Graceful AI failover** — Designing a seamless fallback from cloud AI to a local rules engine without the user noticing a quality drop was a significant UX challenge. We built `LocalRecyclingDatabase` with curated, municipality-specific rules so fallback responses feel just as authoritative.

- **CameraX lifecycle coordination** — Managing the camera lifecycle alongside Compose's declarative lifecycle model required careful coroutine scoping and lifecycle-aware bindings to prevent memory leaks and ensure smooth camera startup/shutdown.

---

## Accomplishments that we're proud of

- **End-to-end AI vision pipeline** — From camera frame capture to structured waste classification in under 3 seconds, leveraging Cloudflare Workers AI (`CloudflareVisionRepository`) and Groq Multimodal Vision with a seamless local fallback.

- **Zero external map API keys** — By using osmdroid and OpenStreetMap, BinMe delivers a fully interactive map experience without requiring Google Maps API keys, billing accounts, or proprietary SDKs.

- **100% Kotlin, 100% Compose** — Not a single XML layout file. The entire UI is declarative Jetpack Compose with Material 3 design tokens, custom glassmorphism surfaces, and fluid Lottie micro-animations.

- **Production-grade architecture** — Clean Architecture + MVVM + UDF in a hackathon project. The codebase is modular, testable, and ready to scale with dependency injection and proper separation of concerns.

- **Municipality-aware intelligence** — BinMe doesn't give generic recycling advice. It's trained and contextualized with real **Lake Oswego and Clackamas County** recycling guidelines, citing official government sources.

- **Resilient by design** — The multi-model failover strategy (trying multiple Qwen models sequentially) and the local rules engine fallback mean BinMe *always* gives users an answer, even offline.

---

## What we learned

- **Prompt engineering is crucial for structured output** — Getting an LLM to consistently return valid, parseable JSON with the exact fields you need requires precise system prompts, explicit schema instructions, and `response_format` enforcement. Vague prompts lead to unpredictable outputs.

- **Image compression matters more than model size** — We initially focused on finding the "best" vision model, but the real accuracy gains came from optimizing how we preprocessed and compressed images before sending them to the API.

- **Compose + imperative Views can coexist** — Integrating legacy Android Views (like osmdroid's `MapView`) into a Jetpack Compose UI is entirely feasible with `AndroidView`, but requires deliberate lifecycle and state management.

- **Offline-first thinking changes everything** — Building the local fallback rules engine wasn't just a safety net — it fundamentally improved the app's reliability and user trust. Users don't care *why* the cloud failed; they care that the app still works.

- **Municipal recycling rules are shockingly complex** — What we assumed would be a simple lookup table turned into a deeply nuanced knowledge base. A pizza box is compost in one county and trash in another. This complexity is exactly why an AI-powered tool like BinMe is needed.

---

## What's next for BinMe

- **🔍 Barcode & UPC Scanning** — Integrate barcode scanning to identify packaged products by their UPC code and pull material composition data from open product databases for even more accurate classification.

- **🌍 Multi-City Expansion** — Extend the municipal rules engine beyond Lake Oswego to support cities across Oregon, then nationwide. Users would set their location and get hyper-local disposal guidance automatically.

- **🏆 Gamification & Leaderboards** — Add community leaderboards, weekly challenges, streak rewards, and shareable eco-impact badges to make sustainable waste sorting social and competitive.

- **📡 Offline Vision Model** — Explore on-device ML models (TensorFlow Lite or ONNX) for waste classification that works entirely offline — no API calls, no latency, full privacy.

- **🏢 Enterprise & Municipal Partnerships** — Package BinMe as a white-label solution for municipal governments, universities, and corporate campuses to deploy to their residents and employees.

- **📱 iOS & Cross-Platform** — Port BinMe to iOS using Kotlin Multiplatform (KMP) or build a companion Flutter/React Native version to reach a broader audience.

- **🔗 Smart Bin IoT Integration** — Connect BinMe with IoT-enabled smart waste bins that can confirm correct disposal, provide fill-level data, and optimize collection routes for waste management services.
