import React from "react";

function Home() {
  return (
    <main className="flex-1 max-w-4xl mx-auto px-6 py-16 space-y-20">
      <section className="text-center space-y-4 py-8">
        <div className="inline-flex items-center space-x-2 bg-emerald-50 text-emerald-700 px-4 py-1.5 rounded-full text-xs font-semibold tracking-wide shadow-xs">
          <span>🎉</span>
          <span>음식물 쓰레기 제로 프로젝트</span>
        </div>
        <h1 className="text-4xl font-extrabold text-slate-900 tracking-tight leading-tight sm:text-5xl">
          우리 동네 식재료 수호대,
          <br />
          <span className="bg-gradient-to-r from-emerald-500 to-teal-600 bg-clip-text text-transparent">
            Food-Guard
          </span>
        </h1>
        <p className="text-base text-slate-500 max-w-xl mx-auto font-medium leading-relaxed">
          버려지는 식재료는 줄이고, 이웃 간의 따뜻한 나눔은 늘리고!
          <br />
          공유 냉장고를 통해 스마트하고 투명하게 음식을 관리해 보세요.
        </p>
      </section>

      {/* 푸터 영역까지 포함하거나 분리 가능 */}
      <footer className="pt-12 border-t border-slate-200 text-center text-xs text-slate-400 font-medium">
        © 2026 Food-Guard. All rights reserved.
      </footer>
    </main>
  );
}

export default Home;
