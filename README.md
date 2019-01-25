# Rocket Launches
An Android app that displays upcoming rocket launch information.

## Overview
This app is a demo of Android architecture and best practices in a situation where the focus is reusable code and long-term
efficiency. Keep in mind that over-engineering/under-engineering and efficiency are entirely dependent on subjective
and objective factors in a project, team, and company, so this setup will not fit the requirements (whether subjective or
objective) of all scenarios. This project is my current view on architecture in such a situation and is always evolving based on 
knowledge gained from experience. By no means should it be taken as gospel, since it will ultimately be improved over time.

## Development Mentality
This project is designed with a mentality opposite of [Extreme Programming](https://en.wikipedia.org/wiki/Extreme_programming) 
(quick initial development but constant refactoring), but without going too far toward over-engineering. This setup comes from a
past of working with startups where resources are highly constrained, deadlines are extremely short, and volatility of app 
features/design and the company's business model is commonplace. Despite my past being specifically with startups, I feel
that this approach can increase efficiency in "normal" situations (where resources are ample) as well. The priorities of the 
project are aimed at maximizing development efficiency and code quality in such a constrained environment and are as follows:
* Prioritize long-term maintainability/scalability and efficiency by having a "write once" mentality. If a little bit of extra 
time has to be spent up front to save more time in the long run, the extra time is well spent. Opportunity cost and return on 
investment should always be taken into consideration. Over-engineering and over-abstraction must still be kept in mind.
* Prioritize long-term maintainability and scalability by limiting technical debt accumulation. Even if a little bit of extra time
is taken to create a solid foundation, the extra time is well spent. Expecting engineers to constantly keep a look out for code to 
rewrite/refactor (ie, fixing any technical debt when it arises) is considered hopeful thinking and is not likely to consistently 
happen; technical debt will eventually creep up due to shortcuts being taken or priorities being placed elsewhere.
* Prioritize long-term testability and efficiency through well-designed code and quality, automated tests, respectively. Manual testing is 
minimized as much as possible due to its flakiness and massive, recurring waste of time both during and after development. Instead,
unit through end-to-end tests are standard so that both both development and QA time (if a QA person/team is present) are 
minimized.
* Prioritize code flexibility due to the volatility of business, product, and/or development requirements.
