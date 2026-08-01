import { expect, test } from '@playwright/test';
import type { Page, TestInfo } from '@playwright/test';

test('a learner can create and review a vocabulary card', async ({ page }, testInfo) => {
  const email = `portfolio-e2e-${testInfo.retry}@example.test`;
  const password = 'Portfolio123!';
  const deckName = 'Dutch B2 portfolio';

  await page.goto('/register');
  await expect(page.getByRole('heading', { name: 'Create your account' })).toBeVisible();

  await page.getByLabel('Email address').fill(email);
  await page.getByLabel('Password').fill(password);
  await page.getByRole('button', { name: 'Create account' }).click();

  await expect(page).toHaveURL(/\/login\?registered=true$/);
  await expect(page.getByRole('status')).toContainText('Your account is ready');

  await page.getByLabel('Email address').fill(email);
  await page.getByLabel('Password').fill(password);
  await page.getByRole('button', { name: 'Sign in' }).click();

  await expect(page).toHaveURL(/\/decks$/);
  await expect(page.getByRole('heading', { name: 'My decks' })).toBeVisible();

  await page.getByRole('button', { name: 'New deck' }).click();
  const deckEditor = page.getByRole('region', { name: 'Create a deck' });
  await deckEditor.getByLabel('Deck name').fill(deckName);
  await deckEditor.getByLabel('Language code').fill('nl');
  await deckEditor.getByRole('button', { name: 'Create deck' }).click();

  const deckCard = page.getByRole('listitem').filter({ hasText: deckName });
  await expect(deckCard).toBeVisible();
  await deckCard.getByRole('link', { name: 'Open' }).click();

  await expect(page).toHaveURL(/\/decks\/\d+$/);
  await expect(page.getByRole('heading', { name: deckName })).toBeVisible();

  await page.getByRole('button', { name: 'Add card' }).click();
  const cardEditor = page.getByRole('region', { name: 'Add a card' });
  await cardEditor.getByLabel('Term').fill('gezellig');
  await cardEditor
    .getByLabel('Definition')
    .fill('Warm, pleasant and comfortable, especially when people are together.');
  await cardEditor.getByLabel('Example sentence (optional)').fill('We hadden een gezellige avond.');
  await cardEditor.getByLabel('CEFR (optional)').selectOption('B2');
  await cardEditor.getByLabel('Tags (optional)').fill('social, adjective');
  await cardEditor.getByRole('button', { name: 'Add card' }).click();

  await expect(page.getByRole('heading', { name: 'gezellig' })).toBeVisible();
  await expect(page.getByText('1 card', { exact: true })).toBeVisible();
  await captureDemo(page, testInfo, '01-card-management.png');
  await page.getByRole('link', { name: 'Start training' }).click();

  await expect(page).toHaveURL(/\/training\?deckId=\d+$/);
  await expect(page.getByRole('heading', { name: 'gezellig' })).toBeVisible();
  await page.getByRole('button', { name: 'Show answer' }).click();
  await expect(
    page.getByText('Warm, pleasant and comfortable, especially when people are together.'),
  ).toBeVisible();
  await captureDemo(page, testInfo, '02-training-answer.png');
  await page.getByRole('button', { name: /Good 7 days/ }).click();

  await expect(page.getByRole('heading', { name: 'You’re caught up' })).toBeVisible();
  await page.getByRole('link', { name: 'Back to deck', exact: true }).click();
  await page.getByRole('link', { name: 'View statistics' }).click();

  await expect(page).toHaveURL(/\/stats\?deckId=\d+$/);
  await expect(page.getByRole('heading', { name: 'Deck statistics' })).toBeVisible();
  await expect(metric(page, 'Total cards').getByText('1', { exact: true })).toBeVisible();
  await expect(metric(page, 'New').getByText('0', { exact: true })).toBeVisible();
  await expect(metric(page, 'Due now').getByText('0', { exact: true })).toBeVisible();
  await expect(metric(page, 'Scheduled').getByText('1', { exact: true })).toBeVisible();
  await expect(page.getByRole('heading', { name: 'You’re caught up' })).toBeVisible();
  await captureDemo(page, testInfo, '03-deck-statistics.png');
});

function metric(page: Page, label: string) {
  return page.getByRole('article').filter({ hasText: label });
}

async function captureDemo(page: Page, testInfo: TestInfo, filename: string) {
  await page.evaluate(() => document.fonts.ready);
  await page.screenshot({
    path: testInfo.outputPath(filename),
    fullPage: true,
  });
}
