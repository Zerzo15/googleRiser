import { useEffect, useMemo, useState } from "react";
import "./App.css";

// Keep local and ngrok previews same-origin; Vite proxies /api to Spring Boot.
// Production can override this with VITE_API_URL when frontend/backend differ.
const API_BASE = import.meta.env.VITE_API_URL || "/api";

const demoProfile = {
  name: "Nova Robotics",
  domain: "novarobotics.ai",
  taxId: "0109 824 662",
  sector: "Robotics & AI",
  scale: "51 – 200 nhân sự",
  market: "Đông Nam Á",
  products:
    "Robot cộng tác, phần mềm điều phối kho và giải pháp tự động hóa cho nhà máy vừa và nhỏ.",
  updated: "2 phút trước",
  completeness: 86,
  sources: [
    {
      name: "Website doanh nghiệp",
      detail: "novarobotics.ai",
      type: "website",
      tone: "blue",
    },
    {
      name: "LinkedIn",
      detail: "Company page · 4.2K followers",
      type: "linkedin",
      tone: "navy",
    },
    {
      name: "Cổng đăng ký kinh doanh",
      detail: "Đã đối soát thông tin",
      type: "registry",
      tone: "yellow",
    },
  ],
};

const demoRecentSearches = [
  {
    name: "Nova Robotics",
    domain: "novarobotics.ai",
    sector: "Robotics & AI",
    status: "Hoàn tất",
    time: "2 phút trước",
    tone: "violet",
  },
  {
    name: "GreenLoop Energy",
    domain: "greenloop.vn",
    sector: "CleanTech",
    status: "Hoàn tất",
    time: "Hôm qua",
    tone: "green",
  },
  {
    name: "Mekong Health",
    domain: "mekonghealth.co",
    sector: "HealthTech",
    status: "Đang xử lý",
    time: "Hôm qua",
    tone: "orange",
  },
  {
    name: "Sora Studio",
    domain: "sorastudio.vn",
    sector: "Design & Media",
    status: "Hoàn tất",
    time: "12/08/2026",
    tone: "pink",
  },
];

const navItems = [
  { id: "overview", label: "Tổng quan", icon: "grid" },
  { id: "profiles", label: "Hồ sơ đã lưu", icon: "folder", count: "24" },
  { id: "sources", label: "Nguồn dữ liệu", icon: "database" },
];

function Icon({ name, size = 18 }) {
  const paths = {
    grid: (
      <>
        <rect x="3" y="3" width="7" height="7" rx="1.5" />
        <rect x="14" y="3" width="7" height="7" rx="1.5" />
        <rect x="3" y="14" width="7" height="7" rx="1.5" />
        <rect x="14" y="14" width="7" height="7" rx="1.5" />
      </>
    ),
    folder: (
      <>
        <path d="M3 7.5A2.5 2.5 0 0 1 5.5 5H10l2 2h6.5A2.5 2.5 0 0 1 21 9.5v7A2.5 2.5 0 0 1 18.5 19h-13A2.5 2.5 0 0 1 3 16.5v-9Z" />
        <path d="M3 10h18" />
      </>
    ),
    database: (
      <>
        <ellipse cx="12" cy="5.5" rx="8" ry="3" />
        <path d="M4 5.5v6c0 1.65 3.58 3 8 3s8-1.35 8-3v-6" />
        <path d="M4 11.5v6c0 1.65 3.58 3 8 3s8-1.35 8-3v-6" />
      </>
    ),
    settings: (
      <>
        <path d="M12 15.1a3.1 3.1 0 1 0 0-6.2 3.1 3.1 0 0 0 0 6.2Z" />
        <path d="m19.4 15 .1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.8 1.8 0 0 0-3.1 1.3v.2a2 2 0 1 1-4 0v-.2a1.8 1.8 0 0 0-3.1-1.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1A1.8 1.8 0 0 0 2.4 12c0-1 .8-1.8 1.8-1.8h.2a1.8 1.8 0 0 0 1.3-3.1l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1A1.8 1.8 0 0 0 11.6 3h.2a2 2 0 1 1 4 0v.2a1.8 1.8 0 0 0 3.1 1.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1A1.8 1.8 0 0 0 23 10.2v.2a2 2 0 1 1 0 4h-.2a1.8 1.8 0 0 0-1.4.6Z" />
      </>
    ),
    search: (
      <>
        <circle cx="10.8" cy="10.8" r="6.8" />
        <path d="m16 16 5 5" />
      </>
    ),
    arrow: (
      <>
        <path d="M5 12h14" />
        <path d="m13 6 6 6-6 6" />
      </>
    ),
    plus: (
      <>
        <path d="M12 5v14M5 12h14" />
      </>
    ),
    download: (
      <>
        <path d="M12 3v12" />
        <path d="m7 10 5 5 5-5" />
        <path d="M5 21h14" />
      </>
    ),
    external: (
      <>
        <path d="M14 4h6v6" />
        <path d="m20 4-9 9" />
        <path d="M18 13v5a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h5" />
      </>
    ),
    check: (
      <>
        <path d="m5 12 4.5 4.5L19 7" />
      </>
    ),
    clock: (
      <>
        <circle cx="12" cy="12" r="8.5" />
        <path d="M12 7v5l3.5 2" />
      </>
    ),
    chevron: <path d="m8 10 4 4 4-4" />,
    sparkle: (
      <>
        <path d="m12 3 1.2 5.8L19 10l-5.8 1.2L12 17l-1.2-5.8L5 10l5.8-1.2L12 3Z" />
        <path d="m19 15 .5 2.5L22 18l-2.5.5L19 21l-.5-2.5L16 18l2.5-.5L19 15Z" />
      </>
    ),
    bell: (
      <>
        <path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9Z" />
        <path d="M10 21h4" />
      </>
    ),
    chevronRight: <path d="m9 18 6-6-6-6" />,
    menu: (
      <>
        <path d="M4 6h16M4 12h16M4 18h16" />
      </>
    ),
    x: (
      <>
        <path d="m6 6 12 12M18 6 6 18" />
      </>
    ),
  };

  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      {paths[name]}
    </svg>
  );
}

function CompanyMark({ name, tone = "violet", small = false }) {
  const initials = name
    .split(" ")
    .slice(0, 2)
    .map((part) => part[0])
    .join("");
  return (
    <div className={`company-mark ${tone} ${small ? "small" : ""}`}>
      {initials}
    </div>
  );
}

function getDomain(value) {
  return (
    value
      .toLowerCase()
      .replace(/https?:\/\//, "")
      .replace(/www\./, "")
      .split("/")[0]
      .replace(/[^a-z0-9.-]/g, "") || "company.vn"
  );
}

function getInitials(value) {
  return (
    value
      .split(" ")
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0])
      .join("")
      .toUpperCase() || "CI"
  );
}

function formatLastUpdated(value) {
  if (!value) return "vừa xong";
  const date = new Date(value);
  return Number.isNaN(date.getTime())
    ? "vừa xong"
    : date.toLocaleString("vi-VN", { dateStyle: "short", timeStyle: "short" });
}

async function apiRequest(path, options = {}) {
  const { token, ...requestOptions } = options;
  const response = await fetch(`${API_BASE}${path}`, {
    ...requestOptions,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...requestOptions.headers,
    },
  });
  const contentType = response.headers.get("content-type") || "";
  const payload = contentType.includes("application/json")
    ? await response.json()
    : await response.text();
  if (!response.ok) {
    const error = new Error(
      typeof payload === "string"
        ? payload
        : payload?.error || payload?.message || "Yêu cầu không thành công",
    );
    error.status = response.status;
    throw error;
  }
  return payload;
}

function mapSources(sources) {
  return sources.map((source) => ({
    name: source.platformName || "Nguồn dữ liệu",
    detail: source.url || "Đã đối soát thông tin",
    type: (source.platformName || "").toLowerCase().includes("linkedin")
      ? "linkedin"
      : "website",
    tone: (source.platformName || "").toLowerCase().includes("linkedin")
      ? "navy"
      : "blue",
  }));
}

function mapHistoryItem(item) {
  const statusMap = {
    COMPLETE: "Hoàn tất",
    PROCESSING: "Đang xử lý",
    PENDING: "Đang xử lý",
    FAILED: "Thất bại",
  };
  return {
    name: item.company?.name || "Doanh nghiệp chưa đặt tên",
    domain: item.company?.domain || "Chưa có tên miền",
    sector: "Company profile",
    status: statusMap[item.status] || "Đang xử lý",
    time: `Tác vụ #${item.id}`,
    tone: "violet",
  };
}

function mapProfile(payload, fallback, sources = []) {
  const company = payload?.company || {};
  const nextSources = sources.length ? mapSources(sources) : fallback.sources;
  return {
    ...fallback,
    name: company.name || fallback.name,
    domain: company.domain || fallback.domain,
    taxId: company.taxId || "Chưa cập nhật",
    sector: payload?.sector || "Chưa cập nhật",
    scale: payload?.scale || "Chưa cập nhật",
    market: payload?.market || "Chưa cập nhật",
    products: payload?.products || "Chưa cập nhật",
    updated: formatLastUpdated(payload?.lastUpdated),
    completeness:
      [
        payload?.sector,
        payload?.scale,
        payload?.products,
        payload?.market,
      ].filter(Boolean).length * 25,
    sources: nextSources,
  };
}

const wait = (duration) =>
  new Promise((resolve) => window.setTimeout(resolve, duration));

function App() {
  const [activeNav, setActiveNav] = useState("overview");
  const [query, setQuery] = useState("Nova Robotics");
  const [domain, setDomain] = useState("novarobotics.ai");
  const [profile, setProfile] = useState(demoProfile);
  const [recentSearches, setRecentSearches] = useState(demoRecentSearches);
  const [isResearching, setIsResearching] = useState(false);
  const [toast, setToast] = useState("");
  const [filter, setFilter] = useState("Tất cả");
  const [mobileNavOpen, setMobileNavOpen] = useState(false);
  const [panelOpen, setPanelOpen] = useState(null);
  const [authOpen, setAuthOpen] = useState(false);
  const [authMode, setAuthMode] = useState("login");
  const [authLoading, setAuthLoading] = useState(false);
  const [authError, setAuthError] = useState("");
  const [authForm, setAuthForm] = useState({
    loginId: "",
    username: "",
    contact: "",
    password: "",
  });
  const [accessToken, setAccessToken] = useState(
    () => window.localStorage.getItem("token") || "",
  );
  const [userName, setUserName] = useState(
    () => window.localStorage.getItem("username") || "Nguyễn Thanh",
  );

  const filteredSearches = useMemo(() => {
    if (filter === "Đã hoàn tất")
      return recentSearches.filter((item) => item.status === "Hoàn tất");
    if (filter === "Đang xử lý")
      return recentSearches.filter((item) => item.status === "Đang xử lý");
    return recentSearches;
  }, [filter, recentSearches]);

  const completedCount = recentSearches.filter(
    (item) => item.status === "Hoàn tất",
  ).length;
  const processingCount = recentSearches.filter(
    (item) => item.status === "Đang xử lý",
  ).length;

  useEffect(() => {
    if (!accessToken) return;
    apiRequest("/companies/history", { token: accessToken })
      .then((history) => setRecentSearches(history.map(mapHistoryItem)))
      .catch((error) => {
        if (error.status === 401 || error.status === 403) {
          window.localStorage.removeItem("token");
          setAccessToken("");
        }
      });
  }, [accessToken]);

  const navigateTo = (destination) => {
    setActiveNav(destination);
    setMobileNavOpen(false);
    const targetId = { profiles: "recent-section", sources: "sources-section" }[
      destination
    ];
    if (targetId)
      document
        .getElementById(targetId)
        ?.scrollIntoView({ behavior: "smooth", block: "start" });
    if (destination === "overview")
      window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const openPanel = (panel) => {
    setPanelOpen(panel);
    setMobileNavOpen(false);
    if (panel === "settings") setActiveNav("settings");
  };

  const upsertRecentSearch = (entry) => {
    setRecentSearches((current) =>
      [entry, ...current.filter((item) => item.name !== entry.name)].slice(
        0,
        6,
      ),
    );
  };

  const notify = (message) => {
    setToast(message);
    window.setTimeout(() => setToast(""), 3200);
  };

  const exportProfile = () => {
    const file = new Blob([JSON.stringify(profile, null, 2)], {
      type: "application/json",
    });
    const link = document.createElement("a");
    link.href = URL.createObjectURL(file);
    link.download = `${getDomain(profile.domain)}-profile.json`;
    link.click();
    URL.revokeObjectURL(link.href);
    notify("Đã xuất hồ sơ doanh nghiệp dạng JSON");
  };

  const openAuth = (mode = "login") => {
    setAuthMode(mode);
    setAuthError("");
    setAuthOpen(true);
  };

  const handleAuthSubmit = async (event) => {
    event.preventDefault();
    setAuthLoading(true);
    setAuthError("");
    try {
      if (authMode === "register") {
        await apiRequest("/user/register", {
          method: "POST",
          body: JSON.stringify({
            username: authForm.username.trim(),
            contact: authForm.contact.trim(),
            password: authForm.password,
          }),
        });
        setAuthMode("login");
        setAuthForm((current) => ({ ...current, loginId: current.username }));
        setAuthError("Tạo tài khoản thành công. Hãy đăng nhập để tiếp tục.");
      } else {
        const response = await apiRequest("/user/login", {
          method: "POST",
          body: JSON.stringify({
            loginId: authForm.loginId.trim(),
            password: authForm.password,
          }),
        });
        window.localStorage.setItem("token", response.token);
        window.localStorage.setItem("username", authForm.loginId.trim());
        setAccessToken(response.token);
        setUserName(authForm.loginId.trim());
        setAuthOpen(false);
        notify("Đăng nhập thành công. Bạn có thể bắt đầu tra cứu.");
      }
    } catch (error) {
      setAuthError(error.message || "Không thể kết nối tới máy chủ");
    } finally {
      setAuthLoading(false);
    }
  };

  const logout = () => {
    window.localStorage.removeItem("token");
    window.localStorage.removeItem("username");
    setAccessToken("");
    setUserName("Nguyễn Thanh");
    setAuthOpen(false);
    notify("Đã đăng xuất khỏi workspace");
  };

  const runResearch = async (event) => {
    event?.preventDefault();
    if (!query.trim()) {
      notify("Hãy nhập tên doanh nghiệp để bắt đầu");
      return;
    }

    const companyName = query.trim();
    const companyDomain = domain.trim() || getDomain(companyName);
    setIsResearching(true);
    setDomain(companyDomain);

    try {
      const controller = new AbortController();
      const timeout = window.setTimeout(() => controller.abort(), 2800);
      const job = await apiRequest("/companies/research", {
        method: "POST",
        body: JSON.stringify({ name: companyName, domain: companyDomain }),
        token: accessToken,
        signal: controller.signal,
      });
      window.clearTimeout(timeout);
      notify(`Đã tạo tác vụ #${job.jobId}. Đang tổng hợp dữ liệu...`);
      upsertRecentSearch({
        name: companyName,
        domain: companyDomain,
        sector: "Company profile",
        status: "Đang xử lý",
        time: `Tác vụ #${job.jobId}`,
        tone: "violet",
      });

      let status = job.status;
      let delay = 1000; // Bắt đầu với 1 giây
      let attempt = 0;

      // Polling with exponential backoff and a 30-attempt safety limit
      while (status !== "COMPLETE" && attempt < 30) {
        await wait(delay);
        const statusResponse = await apiRequest(
          `/companies/jobs/${job.jobId}/status`,
          { token: accessToken },
        );
        status = statusResponse.status;

        if (status === "FAILED") {
          throw new Error("Không thể tổng hợp dữ liệu doanh nghiệp");
        }

        // Tăng delay lên 1.5x mỗi lần thử, tối đa 5 giây
        delay = Math.min(delay * 1.5, 5000);
        attempt += 1;
      }

      if (status !== "COMPLETE")
        throw new Error(
          "Tác vụ tra cứu mất quá nhiều thời gian. Vui lòng thử lại.",
        );

      const [profileResponse, sourcesResponse] = await Promise.all([
        apiRequest(`/companies/${job.companyId}/profile`, {
          token: accessToken,
        }),
        apiRequest(`/companies/${job.companyId}/sources`, {
          token: accessToken,
        }).catch(() => []),
      ]);

      setProfile(
        mapProfile(
          profileResponse,
          { ...demoProfile, name: companyName, domain: companyDomain },
          sourcesResponse,
        ),
      );
      upsertRecentSearch({
        name: companyName,
        domain: companyDomain,
        sector: profileResponse.sector || "Company profile",
        status: "Hoàn tất",
        time: `Tác vụ #${job.jobId}`,
        tone: "violet",
      });
      notify("Đã hoàn tất hồ sơ doanh nghiệp");
    } catch (error) {
      if (error.status === 401 || error.status === 403) {
        openAuth("login");
        notify("Bạn cần đăng nhập để thực hiện tra cứu thật");
      } else if (error.name === "TypeError" || error.name === "AbortError") {
        await wait(850);
        setProfile({
          ...demoProfile,
          name: companyName,
          domain: companyDomain,
          updated: "bản demo",
        });
        upsertRecentSearch({
          name: companyName,
          domain: companyDomain,
          sector: "Company profile",
          status: "Hoàn tất",
          time: "Bản demo",
          tone: "violet",
        });
        notify(
          "Backend chưa chạy — đang hiển thị dữ liệu mẫu để xem trước giao diện",
        );
      } else {
        notify(error.message || "Tra cứu thất bại, vui lòng thử lại");
      }
    } finally {
      setIsResearching(false);
    }
  };

  return (
    <div className="app-shell">
      {mobileNavOpen && (
        <button
          className="sidebar-backdrop"
          aria-label="Đóng menu điều hướng"
          onClick={() => setMobileNavOpen(false)}
        />
      )}
      <aside className={`sidebar ${mobileNavOpen ? "is-open" : ""}`}>
        <div className="brand">
          <div className="brand-mark">
            <span></span>
            <span></span>
            <span></span>
          </div>
          <div>
            <strong>Riser</strong>
            <small>company intelligence</small>
          </div>
        </div>

        <div className="workspace-switcher">
          <div className="workspace-avatar">CI</div>
          <div>
            <span>Workspace</span>
            <strong>Innovation Center</strong>
          </div>
          <Icon name="chevron" size={15} />
        </div>

        <nav className="main-nav" aria-label="Điều hướng chính">
          <p className="nav-label">Không gian làm việc</p>
          {navItems.map((item) => (
            <button
              key={item.id}
              className={`nav-item ${activeNav === item.id ? "active" : ""}`}
              onClick={() => navigateTo(item.id)}
            >
              <Icon name={item.icon} />
              <span>{item.label}</span>
              {item.count && (
                <em>{item.id === "profiles" ? recentSearches.length : item.count}</em>
              )}
            </button>
          ))}
          <p className="nav-label second">Quản trị</p>
          <button
            className={`nav-item ${activeNav === "settings" ? "active" : ""}`}
            onClick={() => openPanel("settings")}
          >
            <Icon name="settings" />
            <span>Cài đặt</span>
          </button>
        </nav>

        <div className="sidebar-bottom">
          <div className="help-card">
            <div className="help-sparkle">
              <Icon name="sparkle" size={15} />
            </div>
            <strong>Cần hỗ trợ?</strong>
            <p>Khám phá cách Riser giúp bạn chuẩn bị cho buổi gặp tiếp theo.</p>
            <button onClick={() => openPanel("help")}>
              <span>Xem hướng dẫn</span>
              <Icon name="arrow" size={14} />
            </button>
          </div>
          <div className="user-card">
            <div className="user-avatar">{getInitials(userName)}</div>
            <div>
              <strong>{userName}</strong>
              <span>{accessToken ? "Đã đăng nhập" : "Research lead"}</span>
            </div>
            <button
              aria-label="Mở menu tài khoản"
              onClick={() => (accessToken ? logout() : openAuth("login"))}
            >
              <Icon name="chevron" size={15} />
            </button>
          </div>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <button
            className="mobile-menu"
            aria-label={mobileNavOpen ? "Đóng menu" : "Mở menu"}
            aria-expanded={mobileNavOpen}
            onClick={() => setMobileNavOpen((open) => !open)}
          >
            <Icon name={mobileNavOpen ? "x" : "menu"} />
          </button>
          <div className="breadcrumbs">
            <span>Workspace</span>
            <Icon name="chevronRight" size={14} />
            <strong>
              {activeNav === "overview"
                ? "Tổng quan"
                : navItems.find((item) => item.id === activeNav)?.label ||
                  "Cài đặt"}
            </strong>
          </div>
          <div className="topbar-actions">
            <span className="live-status">
              <i></i> Hệ thống hoạt động
            </span>
            <button
              className="icon-button"
              aria-label="Thông báo"
              onClick={() => notify("Bạn không có thông báo mới")}
            >
              <Icon name="bell" />
            </button>
            <button
              className="top-avatar"
              aria-label="Mở tài khoản"
              onClick={() => openAuth("login")}
            >
              {getInitials(userName)}
            </button>
          </div>
        </header>

        <div className="page-content">
          <section className="page-heading">
            <div>
              <p className="eyebrow">
                <span className="eyebrow-dot"></span> Hồ sơ doanh nghiệp{" "}
                <span className="eyebrow-line"></span> Cập nhật 22.08.2026
              </p>
              <h1>
                Tra cứu và <span>đối soát doanh nghiệp.</span>
              </h1>
              <p className="heading-copy">
                Tập hợp dữ liệu công khai, kiểm tra nguồn và lưu lại bức tranh
                doanh nghiệp trong một workspace.
              </p>
            </div>
            <div className="heading-actions">
              <button className="button secondary" onClick={exportProfile}>
                <Icon name="download" size={16} /> Xuất hồ sơ
              </button>
              <button
                className="button primary"
                onClick={() =>
                  document.getElementById("research-input")?.focus()
                }
              >
                <Icon name="plus" size={17} /> Hồ sơ mới
              </button>
            </div>
          </section>

          <section className="research-layout">
            <form className="research-card" onSubmit={runResearch}>
              <div className="card-heading">
                <div>
                  <div className="section-kicker">
                    <span className="kicker-icon">
                      <Icon name="search" size={14} />
                    </span>{" "}
                    Tra cứu doanh nghiệp
                  </div>
                  <h2>Bắt đầu một lần tra cứu</h2>
                </div>
                <span className="step-label">
                  01 <i>/</i> 02
                </span>
              </div>
              <div className="form-grid">
                <label className="field field-company">
                  <span>Tên doanh nghiệp</span>
                  <div className="input-wrap">
                    <Icon name="search" size={18} />
                    <input
                      id="research-input"
                      value={query}
                      onChange={(event) => setQuery(event.target.value)}
                      placeholder="VD: Nova Robotics"
                    />
                  </div>
                </label>
                <label className="field">
                  <span>
                    Website hoặc tên miền <b>Tuỳ chọn</b>
                  </span>
                  <div className="input-wrap">
                    <span className="input-prefix">https://</span>
                    <input
                      value={domain}
                      onChange={(event) => setDomain(event.target.value)}
                      placeholder="company.vn"
                    />
                  </div>
                </label>
              </div>
              <div className="research-footer">
                <p>
                  <span className="secure-dot"></span> Kết quả đi kèm nguồn
                  và thời điểm kiểm tra
                </p>
                <button
                  className="button research-button"
                  type="submit"
                  disabled={isResearching}
                >
                  {isResearching ? (
                    <>
                      <span className="spinner"></span> Đang tổng hợp...
                    </>
                  ) : (
                    <>
                      Bắt đầu tra cứu <Icon name="arrow" size={16} />
                    </>
                  )}
                </button>
              </div>
            </form>
            <aside className="today-card">
              <div className="today-pattern"></div>
              <div className="today-top">
                <span className="section-kicker light">Workspace</span>
                <Icon name="arrow" size={17} />
              </div>
              <strong>{recentSearches.length}</strong>
              <h3>
                hồ sơ gần đây
                <br />
                trong workspace
              </h3>
              <div className="today-footer">
                <span>
                  <i className="trend-up">●</i> {completedCount} hoàn tất
                </span>
                <small>{processingCount} đang xử lý</small>
              </div>
            </aside>
          </section>

          <section className="metrics" aria-label="Số liệu tổng quan">
            <div className="metric-card">
              <div className="metric-icon violet">
                <Icon name="folder" size={17} />
              </div>
              <div>
                <span>Tổng hồ sơ</span>
                <strong>{recentSearches.length}</strong>
              </div>
              <em className="neutral">đang theo dõi</em>
            </div>
            <div className="metric-card">
              <div className="metric-icon green">
                <Icon name="check" size={17} />
              </div>
              <div>
                <span>Đã hoàn tất</span>
                <strong>{completedCount}</strong>
              </div>
              <em className="positive">đã đối soát</em>
            </div>
            <div className="metric-card">
              <div className="metric-icon orange">
                <Icon name="clock" size={17} />
              </div>
              <div>
                <span>Đang xử lý</span>
                <strong>{processingCount}</strong>
              </div>
              <em className="neutral">cần xem lại</em>
            </div>
            <div className="metric-card">
              <div className="metric-icon blue">
                <Icon name="database" size={17} />
              </div>
              <div>
                <span>Nguồn đã kết nối</span>
                <strong>{profile.sources.length}</strong>
              </div>
              <em className="neutral">xác minh được</em>
            </div>
          </section>

          <section className="workspace-grid">
            <div className="profile-panel panel" id="profile-section">
              <div className="panel-header">
                <div>
                  <p className="panel-eyebrow">Hồ sơ nổi bật</p>
                  <h2>Thông tin doanh nghiệp</h2>
                </div>
                <button
                  className="more-button"
                  aria-label="Thêm tùy chọn"
                  onClick={() => notify("Hồ sơ đang ở trạng thái mới nhất")}
                >
                  <span></span>
                  <span></span>
                  <span></span>
                </button>
              </div>
              <div className="profile-identity">
                <CompanyMark name={profile.name} />
                <div>
                  <h3>{profile.name}</h3>
                  <a
                    href={`https://${profile.domain}`}
                    target="_blank"
                    rel="noreferrer"
                  >
                    {profile.domain} <Icon name="external" size={13} />
                  </a>
                </div>
                <span className="verified">
                  <Icon name="check" size={13} /> Đã xác minh
                </span>
              </div>
              <div className="profile-fields">
                <div>
                  <span>Lĩnh vực</span>
                  <strong>{profile.sector}</strong>
                </div>
                <div>
                  <span>Quy mô</span>
                  <strong>{profile.scale}</strong>
                </div>
                <div>
                  <span>Thị trường</span>
                  <strong>{profile.market}</strong>
                </div>
                <div>
                  <span>Mã số thuế</span>
                  <strong>{profile.taxId}</strong>
                </div>
              </div>
              <div className="products-field">
                <span>Sản phẩm & dịch vụ</span>
                <p>{profile.products}</p>
              </div>
              <div className="panel-footer">
                <span>
                  <Icon name="clock" size={14} /> Cập nhật {profile.updated}
                </span>
                <button onClick={() => openPanel("detail")}>
                  Xem chi tiết <Icon name="arrow" size={14} />
                </button>
              </div>
            </div>

            <div className="sources-panel panel" id="sources-section">
              <div className="panel-header">
                <div>
                  <p className="panel-eyebrow">Dữ liệu đầu vào</p>
                  <h2>Nguồn thông tin</h2>
                </div>
                <span className="source-count">
                  {profile.sources.length} nguồn
                </span>
              </div>
              <div className="source-list">
                {profile.sources.map((source) => (
                  <div className="source-item" key={source.name}>
                    <div className={`source-icon ${source.tone}`}>
                      {source.type === "website"
                        ? "W"
                        : source.type === "linkedin"
                          ? "in"
                          : "✓"}
                    </div>
                    <div className="source-copy">
                      <strong>{source.name}</strong>
                      <span>{source.detail}</span>
                    </div>
                    <Icon name="check" size={15} />
                  </div>
                ))}
              </div>
              <div className="completeness">
                <div className="completeness-head">
                  <span>Độ đầy đủ hồ sơ</span>
                  <strong>{profile.completeness}%</strong>
                </div>
                <div className="progress">
                  <span style={{ width: `${profile.completeness}%` }}></span>
                </div>
                <p>
                  <Icon name="check" size={13} /> Nguồn hiện có thể kiểm tra
                  lại từ trang chi tiết
                </p>
              </div>
            </div>
          </section>

          <section className="recent-section panel" id="recent-section">
            <div className="recent-header">
              <div>
                <p className="panel-eyebrow">Lịch sử tra cứu</p>
                <h2>Hồ sơ gần đây</h2>
              </div>
              <div className="recent-controls">
                <div className="filter-tabs">
                  {["Tất cả", "Đang xử lý", "Đã hoàn tất"].map((item) => (
                    <button
                      key={item}
                      className={filter === item ? "active" : ""}
                      onClick={() => setFilter(item)}
                    >
                      {item}
                    </button>
                  ))}
                </div>
                <button
                  className="view-all"
                  onClick={() => navigateTo("profiles")}
                >
                  Xem tất cả <Icon name="arrow" size={14} />
                </button>
              </div>
            </div>
            <div className="table-wrap">
              <table>
                <thead>
                  <tr>
                    <th>Doanh nghiệp</th>
                    <th>Lĩnh vực</th>
                    <th>Trạng thái</th>
                    <th>Cập nhật</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {filteredSearches.map((item) => (
                    <tr
                      key={item.name}
                      onClick={() => {
                        setQuery(item.name);
                        setDomain(item.domain);
                        notify(`Đã chọn ${item.name}`);
                      }}
                    >
                      <td>
                        <div className="table-company">
                          <CompanyMark
                            name={item.name}
                            tone={item.tone}
                            small
                          />
                          <div>
                            <strong>{item.name}</strong>
                            <span>{item.domain}</span>
                          </div>
                        </div>
                      </td>
                      <td>
                        <span className="sector-pill">{item.sector}</span>
                      </td>
                      <td>
                        <span
                          className={`status ${item.status === "Hoàn tất" ? "done" : "pending"}`}
                        >
                          <i></i>
                          {item.status}
                        </span>
                      </td>
                      <td className="time-cell">{item.time}</td>
                      <td>
                        <button
                          className="row-arrow"
                          aria-label={`Mở ${item.name}`}
                        >
                          <Icon name="chevronRight" size={16} />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
          <footer className="page-footer">
            <span>
              Riser Intelligence OS <i>•</i> v1.0
            </span>
            <span>Built for faster, better conversations</span>
          </footer>
        </div>
      </main>
      {panelOpen === "help" && (
        <div
          className="modal-backdrop"
          role="presentation"
          onMouseDown={(event) =>
            event.target === event.currentTarget && setPanelOpen(null)
          }
        >
          <section
            className="workspace-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="help-title"
          >
            <button
              className="modal-close"
              aria-label="Đóng"
              onClick={() => setPanelOpen(null)}
            >
              <Icon name="x" size={18} />
            </button>
            <p className="auth-kicker">Hướng dẫn workspace</p>
            <h2 id="help-title">Một lần tra cứu, một hồ sơ có thể kiểm chứng.</h2>
            <p className="auth-copy">
              Riser gom các dấu vết công khai thành một bản ghi ngắn gọn để đội
              ngũ có thể đọc nhanh và quay lại kiểm tra khi cần.
            </p>
            <div className="guide-list">
              <div>
                <b>01</b>
                <span>
                  <strong>Nhập tên hoặc tên miền</strong>
                  Bắt đầu bằng thông tin bạn đang có về doanh nghiệp.
                </span>
              </div>
              <div>
                <b>02</b>
                <span>
                  <strong>Đọc hồ sơ và nguồn</strong>
                  Mỗi kết quả được đặt cạnh nguồn tham chiếu tương ứng.
                </span>
              </div>
              <div>
                <b>03</b>
                <span>
                  <strong>Lưu hoặc xuất bản ghi</strong>
                  Dùng lịch sử tra cứu để tiếp tục công việc ở lần sau.
                </span>
              </div>
            </div>
            <button
              className="button secondary modal-action"
              type="button"
              onClick={() => setPanelOpen(null)}
            >
              Đã hiểu
            </button>
          </section>
        </div>
      )}
      {panelOpen === "settings" && (
        <div
          className="modal-backdrop"
          role="presentation"
          onMouseDown={(event) =>
            event.target === event.currentTarget && setPanelOpen(null)
          }
        >
          <section
            className="workspace-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="settings-title"
          >
            <button
              className="modal-close"
              aria-label="Đóng"
              onClick={() => setPanelOpen(null)}
            >
              <Icon name="x" size={18} />
            </button>
            <p className="auth-kicker">Workspace settings</p>
            <h2 id="settings-title">Cài đặt workspace</h2>
            <p className="auth-copy">
              Kiểm soát cách Riser cập nhật dữ liệu và trình bày kết quả cho
              nhóm của bạn.
            </p>
            <div className="settings-list">
              <label className="setting-row">
                <span>
                  <strong>Tự động làm mới dữ liệu</strong>
                  <small>Kiểm tra lại nguồn khi mở hồ sơ đã lưu.</small>
                </span>
                <input type="checkbox" defaultChecked />
              </label>
              <label className="setting-row">
                <span>
                  <strong>Hiển thị nguồn trong hồ sơ</strong>
                  <small>Giữ liên kết tham chiếu cạnh từng thông tin.</small>
                </span>
                <input type="checkbox" defaultChecked />
              </label>
              <label className="setting-row">
                <span>
                  <strong>Thông báo khi hoàn tất</strong>
                  <small>Nhận tín hiệu khi một lần tra cứu đã xử lý xong.</small>
                </span>
                <input type="checkbox" />
              </label>
            </div>
            <button
              className="button primary modal-action"
              type="button"
              onClick={() => {
                setPanelOpen(null);
                notify("Đã lưu cài đặt workspace");
              }}
            >
              Lưu cài đặt
            </button>
          </section>
        </div>
      )}
      {panelOpen === "detail" && (
        <div
          className="modal-backdrop"
          role="presentation"
          onMouseDown={(event) =>
            event.target === event.currentTarget && setPanelOpen(null)
          }
        >
          <section
            className="workspace-modal detail-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="detail-title"
          >
            <button
              className="modal-close"
              aria-label="Đóng"
              onClick={() => setPanelOpen(null)}
            >
              <Icon name="x" size={18} />
            </button>
            <p className="auth-kicker">Bản ghi doanh nghiệp</p>
            <h2 id="detail-title">{profile.name}</h2>
            <p className="modal-subtitle">
              {profile.domain} · Cập nhật {profile.updated}
            </p>
            <div className="detail-grid">
              <div>
                <span>Mã số thuế</span>
                <strong>{profile.taxId}</strong>
              </div>
              <div>
                <span>Trụ sở</span>
                <strong>{profile.market}</strong>
              </div>
              <div>
                <span>Quy mô</span>
                <strong>{profile.scale}</strong>
              </div>
              <div>
                <span>Lĩnh vực</span>
                <strong>{profile.sector}</strong>
              </div>
            </div>
            <div className="detail-sources">
              <h3>Nguồn đã kiểm tra</h3>
              {profile.sources.map((source) => (
                <div key={source.name}>
                  <span>{source.name}</span>
                  <small>{source.detail}</small>
                </div>
              ))}
            </div>
            <button
              className="button secondary modal-action"
              type="button"
              onClick={() => setPanelOpen(null)}
            >
              Đóng
            </button>
          </section>
        </div>
      )}
      {toast && (
        <div className="toast">
          <span className="toast-check">
            <Icon name="check" size={14} />
          </span>
          {toast}
        </div>
      )}
      {authOpen && (
        <div
          className="modal-backdrop"
          role="presentation"
          onMouseDown={(event) =>
            event.target === event.currentTarget && setAuthOpen(false)
          }
        >
          <section
            className="auth-modal"
            role="dialog"
            aria-modal="true"
            aria-labelledby="auth-title"
          >
            <button
              className="modal-close"
              aria-label="Đóng"
              onClick={() => setAuthOpen(false)}
            >
              <Icon name="x" size={18} />
            </button>
            <div className="auth-symbol">
              <span></span>
              <span></span>
              <span></span>
            </div>
            <p className="auth-kicker">Riser workspace</p>
            <h2 id="auth-title">
              {authMode === "login" ? "Chào mừng trở lại" : "Tạo tài khoản mới"}
            </h2>
            <p className="auth-copy">
              {authMode === "login"
                ? "Đăng nhập để bắt đầu tổng hợp hồ sơ doanh nghiệp."
                : "Tạo workspace cá nhân để lưu lại các hồ sơ đã tra cứu."}
            </p>
            <div className="auth-tabs">
              <button
                className={authMode === "login" ? "active" : ""}
                onClick={() => {
                  setAuthMode("login");
                  setAuthError("");
                }}
              >
                Đăng nhập
              </button>
              <button
                className={authMode === "register" ? "active" : ""}
                onClick={() => {
                  setAuthMode("register");
                  setAuthError("");
                }}
              >
                Đăng ký
              </button>
            </div>
            <form className="auth-form" onSubmit={handleAuthSubmit}>
              {authMode === "register" && (
                <label>
                  <span>Tên người dùng</span>
                  <input
                    value={authForm.username}
                    onChange={(event) =>
                      setAuthForm({ ...authForm, username: event.target.value })
                    }
                    placeholder="nguyen.thanh"
                    required
                  />
                </label>
              )}
              {authMode === "register" && (
                <label>
                  <span>Email hoặc số điện thoại</span>
                  <input
                    value={authForm.contact}
                    onChange={(event) =>
                      setAuthForm({ ...authForm, contact: event.target.value })
                    }
                    placeholder="you@company.vn"
                    required
                  />
                </label>
              )}
              {authMode === "login" && (
                <label>
                  <span>Tên đăng nhập, email hoặc số điện thoại</span>
                  <input
                    value={authForm.loginId}
                    onChange={(event) =>
                      setAuthForm({ ...authForm, loginId: event.target.value })
                    }
                    placeholder="you@company.vn"
                    required
                  />
                </label>
              )}
              <label>
                <span>Mật khẩu</span>
                <input
                  type="password"
                  value={authForm.password}
                  onChange={(event) =>
                    setAuthForm({ ...authForm, password: event.target.value })
                  }
                  placeholder="••••••••"
                  required
                />
              </label>
              {authError && <p className="auth-error">{authError}</p>}
              <button
                className="button auth-submit"
                type="submit"
                disabled={authLoading}
              >
                {authLoading ? (
                  <>
                    <span className="spinner"></span> Đang xử lý...
                  </>
                ) : authMode === "login" ? (
                  "Đăng nhập"
                ) : (
                  "Tạo tài khoản"
                )}
              </button>
            </form>
            {accessToken && (
              <button className="logout-button" onClick={logout}>
                Đăng xuất khỏi workspace
              </button>
            )}
            <p className="auth-note">
              Dữ liệu của bạn được bảo vệ bằng JWT và chỉ dùng trong workspace
              này.
            </p>
          </section>
        </div>
      )}
    </div>
  );
}

export default App;
